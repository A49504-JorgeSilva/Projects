package pt.isel.ls.houses.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.houses.data.mem.BookingRepositoryMem
import pt.isel.ls.houses.data.mem.DbMem
import pt.isel.ls.houses.data.mem.HouseRepositoryMem
import pt.isel.ls.houses.data.mem.UserRepositoryMem
import pt.isel.ls.houses.services.BookingServices
import pt.isel.ls.houses.services.UserServices
import pt.isel.ls.houses.webapi.dtos.BookingResponse
import pt.isel.ls.houses.webapi.dtos.CreateUserRequest
import pt.isel.ls.houses.webapi.dtos.CreateUserResponse
import pt.isel.ls.houses.webapi.dtos.GetUserResponse
import pt.isel.ls.houses.webapi.dtos.LoginRequest
import pt.isel.ls.houses.webapi.dtos.LoginResponse
import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class UserHandlerTests {

    private val userRepo = UserRepositoryMem()
    private val userService = UserServices(userRepo)

    private val houseRepo = HouseRepositoryMem()
    private val bookingRepo = BookingRepositoryMem()
    private val bookingService = BookingServices(bookingRepo, houseRepo)

    private val userHandler = UserHandler(userService, bookingService)

    private lateinit var validToken: String

    @BeforeTest
    fun setUp() {
        DbMem.clearAll()
        val user = userService.createUser("Test User", "test@test.com", "alice123")
        validToken = user.token.toString()
    }

    @Test
    fun `create user returns 201 and token`() {
        val app = routes(
            "/users" bind Method.POST to userHandler::createUser
        )

        val request = Request(Method.POST, "/users")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateUserRequest("John Doe", "john@example.com", "alice123")))

        val response = app(request)

        assertEquals(Status.CREATED, response.status)

        val responseBody = Json.decodeFromString<CreateUserResponse>(response.bodyString())
        assertEquals(2, responseBody.id)    //id now is 2 because of the first user created for test
        assertEquals(true, responseBody.token.isNotBlank())
    }

    @Test
    fun `create user with existing email returns 400`() {
        // Primeiro cria um user
        userService.createUser("John", "john@example.com", "alice123")

        val app = routes(
            "/users" bind Method.POST to userHandler::createUser
        )

        val request = Request(Method.POST, "/users")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateUserRequest("Jane", "john@example.com", "alice123")))

        val response = app(request)

        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `create user with invalid email returns 400`() {
        val app = routes(
            "/users" bind Method.POST to userHandler::createUser
        )

        val request = Request(Method.POST, "/users")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateUserRequest("John", "invalid", "alice123")))

        val response = app(request)

        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `create user with empty name returns 400`() {
        val app = routes(
            "/users" bind Method.POST to userHandler::createUser
        )

        val request = Request(Method.POST, "/users")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateUserRequest("", "john@example.com", "alice123")))

        val response = app(request)

        assertEquals(Status.NOT_FOUND, response.status)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `get user by id returns 200 and correct data`() {
        // Criar um user primeiro
        val created = userService.createUser("John Doe", "john@example.com", "alice123")

        val app = routes(
            "/users/{id}" bind Method.GET to userHandler::getUserById
        )

        val request = Request(Method.GET, "/users/${created.id}")
            .header("Authorization", "Bearer ${created.token}")

        val response = app(request)

        assertEquals(Status.OK, response.status)

        val user = Json.decodeFromString<GetUserResponse>(response.bodyString())
        assertEquals(created.id, user.id)
        assertEquals("John Doe", user.name)
        assertEquals("john@example.com", user.email)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `get user by id with non-existent id returns 404`() {
        val app = routes(
            "/users/{id}" bind Method.GET to userHandler::getUserById
        )
        val dummyUser = userService.createUser("Dummy", "dummy@example.com", "alice123")

        val request = Request(Method.GET, "/users/999")
            .header("Authorization", "Bearer ${dummyUser.token}")

        val response = app(request)

        assertEquals(Status.NOT_FOUND, response.status)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `get user by id with invalid id format returns 400`() {
        val app = routes(
            "/users/{id}" bind Method.GET to userHandler::getUserById
        )

        val dummyUser = userService.createUser("Dummy", "dummy@example.com", "alice123")

        val request = Request(Method.GET, "/users/abc")
            .header("Authorization", "Bearer ${dummyUser.token}")

        val response = app(request)

        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `get user by id without token returns 401`() {
        val app = routes(
            "/users/{id}" bind Method.GET to userHandler::getUserById
        )

        val request = Request(Method.GET, "/users/1")
        val response = app(request)

        assertEquals(Status.UNAUTHORIZED, response.status)
    }

    /*
    ------------------------------
    // listBookingsByUser
    ------------------------------
     */
    @Test
    fun `list bookings by user returns 200 and correct bookings`() {
        // userId=1 has 2 bookings on different houses, userId=2 has 1
        bookingService.createBooking(1, 1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3))
        bookingService.createBooking(2, 1, LocalDate.now().plusDays(5), LocalDate.now().plusDays(7))
        bookingService.createBooking(3, 2, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3))

        val app = routes("/users/{id}/bookings" bind Method.GET to userHandler::listBookingsByUser)

        val request = Request(Method.GET, "/users/1/bookings")
            .header("Authorization", "Bearer $validToken")

        val response = app(request)

        assertEquals(Status.OK, response.status)
        val bookings = Json.decodeFromString<List<BookingResponse>>(response.bodyString())
        assertEquals(2, bookings.size)
        assertTrue(bookings.all { it.userId == 1 })
    }

    @Test
    fun `list bookings by user with no bookings returns 200 and empty list`() {
        // User exists but has no bookings
        val userWithNoBookings = userService.createUser("Empty User", "empty@example.com", "alice123")

        val app = routes("/users/{id}/bookings" bind Method.GET to userHandler::listBookingsByUser)

        val request = Request(Method.GET, "/users/${userWithNoBookings.id}/bookings")
            .header("Authorization", "Bearer $validToken")

        val response = app(request)

        assertEquals(Status.OK, response.status)
        val bookings = Json.decodeFromString<List<BookingResponse>>(response.bodyString())
        assertTrue(bookings.isEmpty())
    }

    @Test
    fun `list bookings by non-existent user returns 404`() {
        val app = routes("/users/{id}/bookings" bind Method.GET to userHandler::listBookingsByUser)

        val request = Request(Method.GET, "/users/99/bookings")
            .header("Authorization", "Bearer $validToken")

        val response = app(request)

        assertEquals(Status.NOT_FOUND, app(request).status)
    }

    @Test
    fun `list bookings by user respects pagination`() {
        bookingService.createBooking(1, 1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))
        bookingService.createBooking(2, 1, LocalDate.now().plusDays(3), LocalDate.now().plusDays(4))
        bookingService.createBooking(3, 1, LocalDate.now().plusDays(5), LocalDate.now().plusDays(6))

        val app = routes("/users/{id}/bookings" bind Method.GET to userHandler::listBookingsByUser)

        val firstPage = app(
            Request(Method.GET, "/users/1/bookings")
                .header("Authorization", "Bearer $validToken")
                .query("skip", "0")
                .query("limit", "2")
        )
        val secondPage = app(
            Request(Method.GET, "/users/1/bookings")
                .header("Authorization", "Bearer $validToken")
                .query("skip", "2")
                .query("limit", "2")
        )

        assertEquals(Status.OK, firstPage.status)
        assertEquals(Status.OK, secondPage.status)
        assertEquals(2, Json.decodeFromString<List<BookingResponse>>(firstPage.bodyString()).size)
        assertEquals(1, Json.decodeFromString<List<BookingResponse>>(secondPage.bodyString()).size)
    }

    @Test
    fun `list bookings by user with invalid id format returns 400`() {
        val app = routes("/users/{id}/bookings" bind Method.GET to userHandler::listBookingsByUser)

        val request = Request(Method.GET, "/users/abc/bookings")
            .header("Authorization", "Bearer $validToken")

        assertEquals(Status.BAD_REQUEST, app(request).status)
    }

    @Test
    fun `list bookings by user without token returns 401`() {
        val app = routes("/users/{id}/bookings" bind Method.GET to userHandler::listBookingsByUser)

        assertEquals(Status.UNAUTHORIZED, app(Request(Method.GET, "/users/1/bookings")).status)
    }

    @Test
    fun `create user with blank password returns 400`() {
        val app = routes(
            "/users" bind Method.POST to userHandler::createUser
        )

        val request = Request(Method.POST, "/users")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateUserRequest("John", "john@example.com", "   ")))

        val response = app(request)

        assertEquals(Status.BAD_REQUEST, response.status)   // novo teste, BAD_REQUEST
    }

    @Test
    fun `create user with short password (less than 6 chars) returns 400`() {
        val app = routes(
            "/users" bind Method.POST to userHandler::createUser
        )

        val request = Request(Method.POST, "/users")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateUserRequest("John", "john@example.com", "12345")))

        val response = app(request)

        assertEquals(Status.BAD_REQUEST, response.status)   // novo teste, BAD_REQUEST
    }

    @Test
    fun `full routes integration - create then get`() {
        val app = routes(
            userHandler.routes()
        )

        // Create
        val createRequest = Request(Method.POST, "/users")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateUserRequest("Alice", "alice@example.com", "alice123")))

        val createResponse = app(createRequest)
        assertEquals(Status.CREATED, createResponse.status)

        val created = Json.decodeFromString<CreateUserResponse>(createResponse.bodyString())

        // Get
        val getRequest = Request(Method.GET, "/users/${created.id}")
            .header("Authorization", "Bearer ${created.token}")

        val getResponse = app(getRequest)
        assertEquals(Status.OK, getResponse.status)

        val user = Json.decodeFromString<GetUserResponse>(getResponse.bodyString())
        assertEquals("Alice", user.name)
        assertEquals("alice@example.com", user.email)
    }

    @Test
    fun `login with correct credentials returns 200 and token`() {
        val app = routes("/login" bind Method.POST to userHandler::login)
        val request = Request(Method.POST, "/login")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(LoginRequest("test@test.com", "alice123")))
        val response = app(request)
        assertEquals(Status.OK, response.status)
        val body = Json.decodeFromString<LoginResponse>(response.bodyString())
        assertEquals(1, body.id)
        assertTrue(body.token.isNotBlank())
    }

    @Test
    fun `login with wrong password returns 401`() {
        val app = routes("/login" bind Method.POST to userHandler::login)
        val request = Request(Method.POST, "/login")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(LoginRequest("test@test.com", "wrong")))
        val response = app(request)
        assertEquals(Status.UNAUTHORIZED, response.status)
    }
}