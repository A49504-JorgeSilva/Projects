# LS Project — Final Phase Report

## Introduction

This document contains the relevant design and implementation aspects of the LS project.

In Phase 1, the system is a **house rental management API** built in Kotlin, using the http4k framework for the web layer and PostgreSQL as the persistence store via plain JDBC. It exposes a RESTful HTTP API that allows clients to manage users, locations, houses, and bookings.

In Phase 2, the system features a Single-Page Application (SPA) frontend built in JavaScript, which uses the API to display data for users, locations, houses, and bookings.

In Phase 3, the system underwent a major architectural refactoring. The Single-Page Application was reorganized into a strict Model-View-Controller (MVC) pattern to improve code quality, maintainability, and separation of concerns. Additionally, the user experience was significantly overhauled to completely abstract internal database IDs from the human user, relying instead on contextual UI elements (like dropdowns) and descriptive titles. Finally, full CRUD UI capabilities were added, including creating locations, houses, and bookings, as well as searching for bookings by date intervals, searching for houses and showing availability to book a house in a specific month.

In Phase 4, Final Phase, the system gained password-based authentication, with dedicated Sign Up and Log In views and session persistence via the browser's `sessionStorage`. The house search was split into two independent filters (title and location name, combinable with AND), a new business rule restricts house creation to `LOCALITY`-type locations, and an autocomplete suggestion list was added to the location field on the house creation form. Finally, the application was containerized with Docker and deployed to Render, becoming publicly accessible.

---

## Modeling the Database

### Conceptual Model

The following diagram describes the Entity-Relationship model for the information managed by the system.

```
┌─────────────┐         ┌─────────────┐         ┌──────────────┐
│    User     │         │   Booking   │         │    House     │
│─────────────│         │─────────────│         │──────────────│
│ id (PK)     │◄────────│ user_id(FK) │────────►│ id (PK)      │
│ name        │  makes  │ house_id(FK)│  for    │ title        │
│ email       │         │ start_date  │         │ area_sqmt    │
│ token (UUID)│         │ end_date    │         │ price/night  │
└────▲────────┘         └─────────────┘         │ description  │
     │                                          │ location_id  │
     └───────────────────────────────────────── │ owner_id (FK)│
                   owns                         └──────┬───────┘
                                                       │ located in
                                                       ▼
                                                ┌──────────────┐
                                                │   Location   │
                                                │──────────────│
                                                │ id (PK)      │
                                                │ name         │
                                                │ type (enum)  │
                                                │ parent_id(FK)│◄─(self)
                                                └──────────────┘
```

We highlight the following aspects:

- A **User** can make zero or more **Bookings**. Each Booking belongs to exactly one User and one House.
- A **House** is associated with exactly one **Location**. A House can have zero or more Bookings.
- A **Location** follows a strict 5-level hierarchy: `COUNTRY > REGION > CITY > MUNICIPALITY > LOCALITY`. Each Location (except a COUNTRY) has a mandatory parent of a strictly higher type — a self-referencing relationship.
- A **User** is uniquely identified by their email address. Authentication is performed via a UUID bearer token assigned at creation time.
- A **User** now also stores a `password` (hashed), enabling credential-based authentication via `POST /login`, in addition to the existing token-based authentication.

The conceptual model has the following restrictions:

- A Booking's `start_date` must be strictly before its `end_date`.
- Bookings for the same house must not overlap in time.
- A Booking cannot be made for past dates.
- Only a `COUNTRY` may be created without a parent location.
- All other location types must have a parent of a strictly higher rank in the hierarchy.
- Two locations with the same name cannot share the same parent.
- A **House** can only be created in a `Location` of type `LOCALITY` (the lowest level of the hierarchy).

---

### Physical Model

The physical model of the database is available in [`createTables.sql`](./createTables.sql).

We highlight the following aspects of this model:

- The `users.token` column uses PostgreSQL's native `UUID` type, which maps directly to Kotlin's `kotlin.uuid.Uuid`.
- The `users.email` column has a `UNIQUE` constraint, enforced both at the database level and in the service layer.
- The `locations.type` column is stored as `VARCHAR(50)`, holding the string name of the `LocationType` enum. There is no DB-level enum, making it easier to evolve the type list without schema migrations.
- The `locations.parent_id` is a nullable self-referencing foreign key. A `NULL` value indicates a top-level location (a COUNTRY).
- The `users.password` column stores a **BCrypt hash** of the user's password, never the plaintext value. Hashing and verification are handled by `org.mindrot.jbcrypt`.
- Date overlap for bookings is enforced in the service/repository layer, not via a DB constraint.
- Tables are dropped and recreated in reverse dependency order in `createTables.sql` to avoid foreign key conflicts during development resets.

---

### Open-API Specification

The Open-API specification is available in [`openapi.yaml`](./openapi.yaml).

In our specification, we highlight the following aspects:

- All endpoints that modify data or access private information are protected by **Bearer token authentication** (`BearerAuth`, UUID format). Creating a user (`POST /users`) is intentionally public — no token is required, as this is how tokens are initially obtained.
- Pagination (`skip` / `limit`) is supported on all list endpoints: `GET /houses`, `GET /bookings`, `GET /bookings/availability`, and `GET /users/{id}/bookings`.
- Full lifecycle management for bookings is documented, including endpoints to update dates (`PUT /bookings/{id}`) and cancel reservations (`DELETE /bookings/{id}`).
- `GET /bookings/availability` returns a `HousesResponse` (same schema as `GET /houses`), allowing clients to reuse the same response model.
- Date parameters are typed as `string / format: date` (ISO 8601 `YYYY-MM-DD`) throughout.
- Error responses document four codes consistently: `400` (bad input), `401` (unauthorized), `404` (not found), `409` (conflict). The location hierarchy violation additionally uses `422 Unprocessable Entity`.
- A new public endpoint `POST /login` authenticates a user by email and password, returning the user's `id` and bearer `token` on success (`200`), or `401 Unauthorized` for invalid credentials.
- `POST /users` now requires a `password` field (minimum 6 characters) in addition to `name` and `email`.
- `GET /houses` supports two independent, combinable filters: `searchTitle` (matches house title) and `searchLocation` (matches the name of the house's location). When both are provided, results must match both (AND).
- `POST /houses` returns `400 Bad Request` if the provided `locationId` does not refer to a `Location` of type `LOCALITY`.
- `GET /locations?name=...` performs a partial, case-insensitive match on location names, used by the SPA to power location autocomplete suggestions.

---

### Request Details

A request flows through the following layers:

```
HTTP Request
    │
    ▼
Handler  (e.g. BookingHandler, HouseHandler)
    │  — Parses Bearer token from Authorization header (if required)
    │  — Deserializes JSON body / reads query and path parameters
    │  — Calls the appropriate Service method
    ▼
Service  (e.g. BookingServices, HouseServices)
    │  — Validates business rules (date ordering, pagination bounds, etc.)
    │  — Calls one or more Repository methods
    ▼
Repository  (e.g. BookingRepositoryPostgres)
    │  — Opens a JDBC connection
    │  — Executes SQL and maps ResultSet rows to domain objects
    ▼
PostgreSQL Database
```

**Classes and functions involved in a typical request (`POST /bookings`):**

1. `BookingHandler.createBooking(request)` — extracts and validates the Bearer token via `UserServices.getUserByToken()`, deserializes `CreateBookingRequest`, parses date strings to `LocalDate`.
2. `BookingServices.createBooking(houseId, userId, startDate, endDate)` — checks `startDate < endDate`, checks start is not in the past, calls `BookingRepository.getOverlappingBookings()` to enforce no date conflicts.
3. `BookingRepositoryPostgres.create(...)` — executes `INSERT INTO bookings` and returns the created `Booking` domain object using the auto-generated key.
4. Back in the handler, the domain object is mapped to `CreateBookingResponse` and serialized to JSON with `201 Created`.

**Parameter validation** happens at two levels:

- **Handler level**: structural checks — token UUID format, path `id` parseability (`toIntOrNull()`), date string parseability, JSON deserialization errors. These return `400` or `401`.
- **Service level**: semantic/business checks — `require(startDate.isBefore(endDate))`, `require(skip >= 0)`, `check(overlaps.isEmpty())`, blank string checks, email format, and location hierarchy validation.

---

### Connection Management

The data access layer uses a DataSource for connection management. In HousesServer.kt, a PGSimpleDataSource is instantiated and injected via constructor into all Postgres repository implementations (UserRepositoryPostgres, BookingRepositoryPostgres, etc.). Every database operation acquires a connection via dataSource.connection.use { conn -> ... }, ensuring connections are safely closed after execution. While we are currently using a simple data source, this dependency injection pattern makes it trivial to swap to a connection pool (like HikariCP) in future phases.

---

### Data Access

The data access layer is structured around four repository interfaces (`BookingRepository`, `HouseRepository`, `LocationRepository`, `UserRepository`) with Postgres implementations in the `data.postgres` subpackage.

**Non-trivial SQL statements:**

- **Overlap detection** (`getOverlappingBookings`, `getBookedHouseIds`):

  ```sql
  SELECT * FROM bookings
  WHERE house_id = ? AND start_date < ? AND end_date > ?
  ```

  Detects any booking whose interval overlaps `[start, end)` using the standard predicate: intervals `[a,b)` and `[c,d)` overlap iff `a < d AND b > c`. Parameters are bound as `(end, start)`.

- **Paginated bookings within a date range** (`getBookingsByHouseAndDates`):

  ```sql
  SELECT * FROM bookings
  WHERE house_id = ? AND start_date >= ? AND end_date <= ?
  OFFSET ? LIMIT ?
  ```

  Filters bookings **fully contained** within the requested window, then applies `OFFSET`/`LIMIT` pagination.

- **Availability set** (`getBookedHouseIds`):

  ```sql
  SELECT house_id FROM bookings
  WHERE start_date < ? AND end_date > ?
  ```

  Returns all house IDs with at least one conflicting booking. The service then excludes this set from all houses to compute availability.

- **Filtered house search** (`getAllHouses`):

  ```sql
    SELECT h.* FROM houses h
    LEFT JOIN locations l ON h.location_id = l.id
    WHERE LOWER(h.title) LIKE LOWER(?) AND LOWER(l.name) LIKE LOWER(?)
    OFFSET ? LIMIT ?
  ```

  The `WHERE` clause is built dynamically: each filter (`searchTitle`, `searchLocation`) only contributes a condition if its corresponding query parameter is present, and when both are present they are combined with `AND`.

- **Location autocomplete** (`getByPartialName`):
  
  ```sql
    SELECT id, name, type, parent_id FROM locations
    WHERE name LIKE CONCAT('%', ?, '%')
  ```
  
  Returns all locations whose name contains the given substring, regardless of type or hierarchy level.
All queries use `PreparedStatement` with positional parameters to prevent SQL injection. The `RETURN_GENERATED_KEYS` flag is used on all `INSERT` statements to retrieve the auto-generated primary key without an extra round-trip.

---

### Error Handling / Processing

The project uses a layered error handling strategy.

**Domain exceptions** (defined in `Exceptions.kt`) are thrown by the service layer for business-rule violations:

| Exception | Meaning | HTTP Status |
|---|---|---|
| `InvalidInputException` | Blank fields, invalid email, bad location type | `400 Bad Request` |
| `AuthenticationException` | Wrong password during login | `401 Unauthorized` |
| `NotFoundException` | Entity with given ID does not exist | `404 Not Found` |
| `ConflictException` | Duplicate email, duplicate location name under same parent | `409 Conflict` |
| `InvalidHierarchyException` | Location type invalid for the given parent | `422 Unprocessable Entity` |

**Authentication flow**: `UserServices.login(email, password)` looks up the user by email, then verifies the password with `BCrypt.checkpw()` against the stored hash. A mismatch throws `AuthenticationException`, mapped to `401`. A non-existent email throws `NotFoundException` (`404`). On success, the existing `User.token` is returned, allowing the same UUID bearer token used since Phase 1 to authenticate subsequent requests.

**House location validation**: `HouseServices.createNewHouse` now depends on `LocationRepository` in addition to `HouseRepository`. Before creating a house, it fetches the target `Location` and verifies its `type == LocationType.LOCALITY`, throwing `InvalidInputException` (`400`) otherwise.

**Kotlin standard exceptions** used in services:
- `IllegalArgumentException` (via `require`) — for invalid input parameters such as bad date ordering or negative pagination values.
- `IllegalStateException` (via `check`) — for violated internal state assumptions, e.g. overlapping bookings exist.
- `NoSuchElementException` — thrown when a repository returns `null` for a lookup by ID.

**Handler-level handling**: Each handler wraps its execution in the `executeHandler` higher-order function, which acts as a centralized `try/catch` block. It automatically maps domain exception types to standard HTTP status codes. JSON deserialization and `LocalDate.parse` failures return `400 Bad Request`. Unrecognised exceptions also fall through to `400` as a safe default.

---

## Single-Page Application

To complement the backend, a Single-Page Application was developed, which provides a dynamic interface where clients can manage users, locations, houses, and bookings.
In Phase 3, the monolithic `handlers.js` file was critically reviewed and completely refactored to improve code quality and testability. The SPA now strictly adheres to a modular architecture, splitting responsibilities across three distinct layers:
- Services (`spa/services/`): Responsible for all external communication. These files exclusively handle fetch requests to the API, attach the Bearer token, and parse the JSON responses. They act as the data layer for the frontend.
- Views (`spa/views/`): Purely presentational logic. These functions take data objects and use dsl.js to construct and return HTML DOM elements. They are entirely unaware of the API or the routing mechanism.
- Handlers (`spa/handlers/`): The controllers of the application. They intercept the route parameters, call the appropriate Service to fetch data, pass that data to the View to build the UI, and then attach the resulting DOM elements to the `mainContent` container.

### Application Structure

The following diagram holds the application's structure for a typical client session:

![Alt text](https://raw.githubusercontent.com/wiki/isel-leic-ls/2526-2-common/docs/Navi.svg)

We highlight the following aspects of this structure:
- The **Home** is accessible from every page in the application.
- The **Houses** page supports pagination. It lists two houses at a time, and the remaining houses can be displayed via the next and prev buttons.
- The **Bookings** page can list all bookings for either a user or a house, depending on which page the client came from.

### Software Organization

The application uses a simple client-side routing mechanism based on the URL hash to switch between views without reloading the page. The following diagram describes how our JavaScript files work together to achieve this:
```
index.html (Entry point, loads index.js)
    │
    ▼
index.js (Registers routes to handlers)
    │
    ├─► router.js (Intercepts URL Hash, extracts params)
    │
    └─► handlers.js (Executes fetch, builds UI using dsl.js)
           │
           ▼
        dsl.js (Declarative UI engine)
```

The following table maps the application's dynamic URLs to their respective handler functions in handlers.js:

| URL Hash | Handler Function | Description |
| :--- | :--- | :--- |
| `#home` | `getHome()` | Renders the landing page. |
| `#houses` | `getHouses()` | Fetches and lists all available houses (with pagination). |
| `#houses/:id` | `getHouseDetails()` | Displays detailed information for a specific house. |
| `#locations/:id` | `getLocationDetails()` | Displays details about a specific location in the hierarchy. |
| `#users/:id` | `getUserDetails()` | Shows a user's profile information. |
| `#houses/:hid/bookings` | `getBookings()` | Lists all bookings made for a specific house. |
| `#users/:id/bookings` | `getUserBookings()` | Lists all bookings made by a specific user. |
| `#bookings/:id` | `getBookingDetails()` | Displays the details of a specific booking. |
| *(Unknown Route)* | `DefaultNotFoundHandler` | Redirects any unknown URL hashes back to `#home`. |


### Routing
The routing of the SPA was done using dynamic routing (using the **hash** location).
This was done so that the **SPA** could be hosted on a **static server** and the **hash** would be used to
**change the content** of the page without having to fully reload it.

Example:
```
localhost:8080/#houses
```
will update the HTML to present the list of all houses, without having to reload the page itself.

To manage the actions associated with each URL hash, the system imports functions from **handlers.js**. 
These functions map specific **handlers** to their corresponding **routes**. 
This setup ensures that whenever a request is made to a URL containing dynamic parameters, the application correctly updates 
and displays the relevant content.

Example:
```
localhost:8080/#users/2
```
Will update the HTML to present the information of the user with the user_id 2.

### Sign Up, Log In, and Session Management

Phase 4 introduces credential-based authentication on top of the existing token mechanism:

- **Sign Up** (`buildSignUpView` / `getSignUpView`): a form collecting name, email, and password, submitted to `POST /users`. On success, the returned `{id, token}` is stored immediately and the user is redirected to their profile — i.e. **auto-login on registration**, avoiding a redundant separate login step.
- **Log In** (`buildLoginView` / `getLoginView`): a form collecting email and password, submitted to `POST /login`. On success, the returned `{id, token}` is stored the same way.
- **Session storage** (`spa/utils/token.js`): the token and user id are stored in `sessionStorage` rather than hardcoded. The session persists across page reloads and route changes but is cleared automatically when the browser tab/window is closed, or explicitly via **Logout**.
- **Dynamic Navbar** (`spa/utils/navbar.js`): the navigation bar reflects authentication state on every page. When logged out, it shows "Log In" and "Sign Up" buttons; when logged in, it shows "My Profile" (linking to `#users/:id`) and "Logout".

### Location Autocomplete on House Creation

To help users select a valid location without knowing its internal id, the "Create House" form (`houseViews.js` / `houseHandlers.js`) attaches an HTML `<datalist>` to the location input field. On every keystroke, once at least 3 characters have been typed, the SPA calls `fetchLocationsByName(query)`, which performs `GET /locations?name=...`. The returned locations populate the `<datalist>` as suggestions, and the browser displays them as a native dropdown. On form submission, the typed text is matched (case-insensitively) against the fetched locations to resolve the corresponding `locationId`.

### Split House Search

The Home page's search functionality, previously a single combined field, was split into two independent inputs: one matching the **house title** (`searchTitle`) and another matching the **location name** (`searchLocation`). Each field can be used on its own; when both are filled, the backend combines them with a logical **AND**, returning only houses whose title matches the first filter *and* whose location name matches the second. This is implemented via `fetchHousesFiltered(searchTitle, searchLocation, skip, limit)` in `houseServices.js`, which builds the appropriate query string for `GET /houses`.
### Rendering HTML components with JavaScript

The update of the page's content was done using **javascript** functions in the file **dsl.js**.
These functions are helper functions that are exported to the handlers.js file and are used to render HTML components.

### Resource Management
The **SPA** uses the **API** to **interact with the database** and **retrieve the required information**.
This is done by using the **fetch** method everytime the client wants to perform an operation, which allows the **SPA** to **interact with the API** and
**retrieve the required information**.

Example:

```
fetch(API_BASE_URL + "/houses/" + id, {
        headers: {
            "Authorization": "Bearer " + getToken()
        }
    })
```

this fetch method is made everytime the client wants to list all the information of a desired house.

### Authentication

The authentication revolves around a token (stored in the **token.js** file). The token is associated with a user at creation time, which is then used to authenticate all later requests.

### Hiding Internal IDs from the User
A major requirement of Phase 3 was ensuring the human user never interacts with or sees database IDs. This was achieved by:
- **Dropdown Selectors**: Forms for creating entities (like the "Create House" form) use `<select>` dropdowns populated dynamically with Location names via the API, passing the locationId silently upon submission.
- **Asynchronous Enrichment**: Handlers such as getUserBookings utilize `Promise.all` to fetch contextual data (like House Title) based on foreign keys before passing the data to the View, replacing raw IDs with human-readable titles.
- **Contextual Navigation & Search**: Action buttons (like "Book this House") and specific filters (like the "Search Bookings by Date" form) are placed directly on the entity's detail page, allowing the router to silently extract the necessary ID from the URL hash without requiring manual user input.

## Deployment

The application was containerized with Docker and deployed to [Render](https://render.com), making it publicly accessible at `https://service-ls-2526-2-leic43d-g04.onrender.com`.

- **Build**: `build.gradle.kts` was configured to produce a *fat JAR* — a single executable JAR bundling the application code and all runtime dependencies (http4k, PostgreSQL driver, etc.), via a `copyRuntimeDependencies` task and a `Class-Path` manifest entry pointing to `pt.isel.ls.houses.server.HousesServerKt` as the main class.
- **Containerization**: a `Dockerfile` based on `eclipse-temurin:21` copies the built JAR and the SPA's static content, exposing port `8080` and running the server via `java -jar`.
- **Configuration via environment variables**: `DbConnection.kt` reads `JDBC_DATABASE_URL` (provided by Render) to connect to the managed PostgreSQL instance, falling back to a local connection string for development. Similarly, the server port falls back to `8080` if `PORT` is not set, ensuring the application runs unchanged both locally and on Render.
- **Database**: a managed PostgreSQL instance was provisioned on Render, with the schema created from `createTables.sql`.

## Quality Assurance & Testing

The testing strategy for the project is divided into backend server-side validation and frontend client-side resilience, ensuring high confidence in both business logic and UI integrity.
* **Authentication Tests:** `UserServicesTests.kt` covers password hashing and verification (`createUser`, `login`), including correct credentials, wrong password (`AuthenticationException`), and non-existent email (`NotFoundException`).
* **House Location Validation:** `HouseServicesTest.kt` and `HouseHandlerTests.kt` verify that `createNewHouse` rejects locations that are not of type `LOCALITY` (`InvalidInputException` / `400`), and accept the happy path with a valid `LOCALITY`.
* **Filtered Search:** updated tests cover `searchTitle` and `searchLocation` independently and combined (AND), replacing the Phase 3 single-field search and its now-obsolete `locationTypes` filter (no longer meaningful once houses are restricted to `LOCALITY`).

### Backend Testing (Kotlin/JVM)
The backend test suite ensures the integrity of domain rules, service-layer logic, and API contract compliance:
* **Service-Layer Unit Tests:** Comprehensive tests (e.g., `BookingServicesTest.kt`, `LocationServicesTest.kt`) use an In-Memory database (`DbMem`) to validate domain invariants, such as booking overlaps, location hierarchy rules, and pagination constraints in isolation.
* **Web API Integration Tests:** Handler tests (e.g., `BookingHandlerTests.kt`, `LocationHandlerTests.kt`) verify the HTTP layer, ensuring that requests are correctly parsed, domain exceptions are mapped to appropriate HTTP status codes (400, 404, 409, 422), and authentication headers are properly validated.

### Frontend Testing (JavaScript/SPA)
With the migration to the Model-View-Controller (MVC) architecture, the frontend testing suite was significantly expanded to validate the new modular structure:
* **Service-Layer Unit Tests:** New suites (`test.bookingServices.js`, `test.houseServices.js`, `test.locationServices.js`, `test.userServices.js`) provide 100% coverage for API interaction logic. Using **Mocha** and **Chai**, we isolate the service layer by mocking the native `fetch` API. This allows us to verify correct URL construction, request payload serialization, token injection, and graceful handling of server-side errors (401, 404, 500) without needing a live network.
* **Router & Navigation Tests:** The `test.router.js` suite validates the SPA's navigation logic, ensuring that route templates correctly extract dynamic path parameters (`:id`, `:hid`) and that unknown routes are consistently handled by the default 404 redirect.

All frontend tests are executed within the browser via a dedicated Mocha test runner (`/tests/index.html`), providing an immediate feedback loop for UI logic changes.

---

## Critical Evaluation

**Missing functionality:**

- There is no missing functionality. All Phase 3 requirements, including search filters, date-interval booking searches on the house view, location/house/booking creation forms, and full ID abstraction, are fully implemented.

**Known defects:**
- There are currently no automated integration tests for the raw SQL/JDBC queries in the repository layer. Reliability is currently verified through manual testing via the OpenAPI interface and .http scripts.
- While the SPA architecture is vastly improved, the heavy reliance on Promise.all for data enrichment in the Handlers (e.g., fetching house titles for a list of bookings) can lead to the "N+1 query problem" on the frontend, potentially causing slower render times on high-latency networks.

**Future Improvements:**

- API Aggregation: Modify the Kotlin backend to return fully enriched DTOs (e.g., including the houseTitle directly in the BookingResponse) to reduce the number of concurrent HTTP requests the frontend must make.
- Implement an In-Memory Cache for frequent reads (e.g., House Details) to reduce database load.
- Filter location autocomplete suggestions to only `LOCALITY`-type locations when used in the house creation form, avoiding suggestions that would later be rejected by the backend.