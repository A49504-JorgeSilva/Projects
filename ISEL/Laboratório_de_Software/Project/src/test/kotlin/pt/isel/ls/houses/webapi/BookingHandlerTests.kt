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
import pt.isel.ls.houses.data.mem.LocationRepositoryMem
import java.time.LocalDate
import pt.isel.ls.houses.data.mem.UserRepositoryMem
import pt.isel.ls.houses.domain.LocationType
import pt.isel.ls.houses.services.BookingServices
import pt.isel.ls.houses.services.HouseServices
import pt.isel.ls.houses.services.UserServices
import pt.isel.ls.houses.webapi.dtos.AvailableDaysResponse
import pt.isel.ls.houses.webapi.dtos.CreateBookingRequest
import pt.isel.ls.houses.webapi.dtos.CreateBookingResponse
import pt.isel.ls.houses.webapi.dtos.BookingResponse
import pt.isel.ls.houses.webapi.dtos.UpdateBookingRequest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

class BookingHandlerTests {

    private val userRepo = UserRepositoryMem()
    private val locationRepo = LocationRepositoryMem()
    private val userService = UserServices(userRepo)

    private val houseRepo = HouseRepositoryMem()
    private val houseService = HouseServices(houseRepo, locationRepo)

    private val bookingRepo = BookingRepositoryMem()
    private val bookingService = BookingServices(bookingRepo, houseRepo)
    private val bookingHandler = BookingHandler(bookingService, userService)

    private lateinit var validToken: String
    private lateinit var userId: String

    @OptIn(ExperimentalUuidApi::class)
    @BeforeTest
    fun setUp() {
        DbMem.clearAll()
        repeat(4) { locationRepo.create("Locality ${it + 1}", LocationType.LOCALITY, null) }

        //create a valid User to get a real token for the Authorization header
        val user = userService.createUser("Test User", "test@test.com", "alice123")
        validToken = user.token.toString()
        userId = user.id.toString()
    }

    @Test
    fun `create booking returns 201 and booking id`() {
        val app = routes(
            "/bookings" bind Method.POST to bookingHandler::createBooking
        )

        val requestBody = CreateBookingRequest(1, "2026-07-01", "2026-07-10")

        val request = Request(Method.POST, "/bookings")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(requestBody))

        val response = app(request)

        assertEquals(Status.CREATED, response.status)

        val responseBody = Json.decodeFromString<CreateBookingResponse>(response.bodyString())
        assertEquals(1, responseBody.id)
    }

    @Test
    fun `create booking with missing token returns 401`() {
        val app = routes(
            "/bookings" bind Method.POST to bookingHandler::createBooking
        )

        val requestBody = CreateBookingRequest(1, "2026-07-15", "2026-07-20")

        val request = Request(Method.POST, "/bookings")
            .header("Content-Type", "application/json")
            //omitting Authorization header
            .body(Json.encodeToString(requestBody))

        val response = app(request)

        assertEquals(Status.UNAUTHORIZED, response.status)
    }

    @Test
    fun `create booking with overlapping dates returns 400`() {
        //create the first booking successfully directly via service
        bookingService.createBooking(1, 1, LocalDate.parse("2026-07-05"), LocalDate.parse("2026-07-12"))

        val app = routes(
            "/bookings" bind Method.POST to bookingHandler::createBooking
        )

        //try to overlap it
        val requestBody = CreateBookingRequest(1, "2026-07-05", "2026-07-12")

        val request = Request(Method.POST, "/bookings")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(requestBody))

        val response = app(request)

        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `get booking by id returns 200 and correct data`() {
        //create a booking first
        val created = bookingService.createBooking(1, 1, LocalDate.parse("2026-07-01"), LocalDate.parse("2026-07-10"))

        val app = routes(
            "/bookings/{id}" bind Method.GET to bookingHandler::getBookingById
        )

        val request = Request(Method.GET, "/bookings/${created.id}")
            .header("Authorization", "Bearer $validToken")
        val response = app(request)

        assertEquals(Status.OK, response.status)

        val booking = Json.decodeFromString<BookingResponse>(response.bodyString())
        assertEquals(created.id, booking.id)
        assertEquals(1, booking.houseId)
        assertEquals(1, booking.userId)
    }

    @Test
    fun `get booking by id with non-existent id returns 404`() {
        val app = routes(
            "/bookings/{id}" bind Method.GET to bookingHandler::getBookingById
        )

        val request = Request(Method.GET, "/bookings/999")
            .header("Authorization", "Bearer $validToken")
        val response = app(request)

        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `list bookings by house id returns 200`() {
        bookingService.createBooking(1, 1, LocalDate.parse("2026-07-01"), LocalDate.parse("2026-07-10"))

        val app = routes(
            "/bookings" bind Method.GET to bookingHandler::listBookings
        )

        val request = Request(Method.GET, "/bookings").query("hid", "1")
            .header("Authorization", "Bearer $validToken")
        val response = app(request)

        assertEquals(Status.OK, response.status)
    }

    @Test
    fun `get booking by id with invalid id format returns 400`() {
        val app = routes(
            "/bookings/{id}" bind Method.GET to bookingHandler::getBookingById
        )

        val request = Request(Method.GET, "/bookings/abc")
            .header("Authorization", "Bearer $validToken")
        val response = app(request)

        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `list bookings with date filters and pagination returns 200`() {
        val app = routes(
            "/bookings" bind Method.GET to bookingHandler::listBookings
        )

        val request = Request(Method.GET, "/bookings")
            .header("Authorization", "Bearer $validToken")
            .query("hid", "1")
            .query("startDate", "2026-05-01")
            .query("endDate", "2026-07-01")
            .query("skip", "0")
            .query("limit", "5")

        val response = app(request)

        assertEquals(Status.OK, response.status)
    }

    @Test
    fun `list bookings missing mandatory house id returns 400`() {
        val app = routes(
            "/bookings" bind Method.GET to bookingHandler::listBookings
        )

        //missing the "hid" query parameter entirely
        val request = Request(Method.GET, "/bookings")
            .header("Authorization", "Bearer $validToken")
        val response = app(request)

        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `get available houses returns 200 and list of houses`() {
        houseService.createNewHouse("Test House", 1, 100.0, 50.0, "Nice view", userId.toInt())

        val app = routes(
            "/bookings/available" bind Method.GET to bookingHandler::getAvailableHouses
        )

        val request = Request(Method.GET, "/bookings/available")
            .query("startDate", "2026-08-01")
            .query("endDate", "2026-08-15")
            .query("skip", "0")
            .query("limit", "10")

        val response = app(request)

        assertEquals(Status.OK, response.status)
        assertTrue(response.bodyString().contains("Test House"))
    }

    @Test
    fun `get available houses missing mandatory dates returns 400`() {
        val app = routes(
            "/bookings/available" bind Method.GET to bookingHandler::getAvailableHouses
        )

        //missing endDate
        val request = Request(Method.GET, "/bookings/available")
            .query("startDate", "2026-08-01")

        val response = app(request)

        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `get available houses with invalid date format returns 400`() {
        val app = routes(
            "/bookings/available" bind Method.GET to bookingHandler::getAvailableHouses
        )

        val request = Request(Method.GET, "/bookings/available")
            .query("startDate", "not-a-date")
            .query("endDate", "2026-08-15")

        val response = app(request)

        assertEquals(Status.BAD_REQUEST, response.status)
        assertTrue(response.bodyString().contains("Invalid date format"))
    }

    @Test
    fun `get available houses does not return houses that are already booked`() {
        val availableHouseId = houseService.createNewHouse("Free House", 1, 100.0, 50.0, "Available", userId.toInt())
        val bookedHouseId = houseService.createNewHouse("Taken House", 1, 100.0, 50.0, "Booked", userId.toInt())

        bookingService.createBooking(
            houseId = bookedHouseId,
            userId = 1,
            startDate = LocalDate.parse("2026-08-01"),
            endDate = LocalDate.parse("2026-08-10")
        )

        val app = routes(
            "/bookings/available" bind Method.GET to bookingHandler::getAvailableHouses
        )

        val request = Request(Method.GET, "/bookings/available")
            .query("startDate", "2026-08-01")
            .query("endDate", "2026-08-10")
            .query("skip", "0")
            .query("limit", "10")

        val response = app(request)

        assertEquals(Status.OK, response.status)
        val responseBody = response.bodyString()

        assertTrue(responseBody.contains("Free House"))
        assertFalse(responseBody.contains("Taken House"))
    }

    @Test
    fun `get booking by id without token returns 401`() {
        val app = routes(
            "/bookings/{id}" bind Method.GET to bookingHandler::getBookingById
        )

        val request = Request(Method.GET, "/bookings/1")
        val response = app(request)

        assertEquals(Status.UNAUTHORIZED, response.status)
    }

    @Test
    fun `list bookings without token returns 401`() {
        val app = routes(
            "/bookings" bind Method.GET to bookingHandler::listBookings
        )

        val request = Request(Method.GET, "/bookings").query("hid", "1")
        val response = app(request)

        assertEquals(Status.UNAUTHORIZED, response.status)
    }

    /*
    ---------------------------------------------
    // deleteBooking
    ---------------------------------------------
     */

    @Test
    fun `delete booking returns 204 on success`() {
        val created = bookingService.createBooking(1, 1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(5))

        val app = routes("/bookings/{id}" bind Method.DELETE to bookingHandler::deleteBooking)

        val request = Request(Method.DELETE, "/bookings/${created.id}")
            .header("Authorization", "Bearer $validToken")

        assertEquals(Status.NO_CONTENT, app(request).status)
    }

    @Test
    fun `delete booking with non-existent id returns 404`() {
        val app = routes("/bookings/{id}" bind Method.DELETE to bookingHandler::deleteBooking)

        val request = Request(Method.DELETE, "/bookings/999")
            .header("Authorization", "Bearer $validToken")

        assertEquals(Status.NOT_FOUND, app(request).status)
    }

    @Test
    fun `delete booking with invalid id format returns 400`() {
        val app = routes("/bookings/{id}" bind Method.DELETE to bookingHandler::deleteBooking)

        val request = Request(Method.DELETE, "/bookings/abc")
            .header("Authorization", "Bearer $validToken")

        assertEquals(Status.BAD_REQUEST, app(request).status)
    }

    @Test
    fun `delete booking without token returns 401`() {
        val app = routes("/bookings/{id}" bind Method.DELETE to bookingHandler::deleteBooking)

        assertEquals(Status.UNAUTHORIZED, app(Request(Method.DELETE, "/bookings/1")).status)
    }


    /*
    ----------------------------------------------------------
    // UpdateBooking
    ----------------------------------------------------------
     */


    @Test
    fun `update booking returns 200 and updated data`() {
        val created = bookingService.createBooking(1, 1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(5))

        val app = routes("/bookings/{id}" bind Method.PUT to bookingHandler::updateBooking)

        val newStart = LocalDate.now().plusDays(10).toString()
        val newEnd = LocalDate.now().plusDays(15).toString()

        val request = Request(Method.PUT, "/bookings/${created.id}")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(UpdateBookingRequest(newStart, newEnd)))

        val response = app(request)

        assertEquals(Status.OK, response.status)
        val updated = Json.decodeFromString<BookingResponse>(response.bodyString())
        assertEquals(created.id, updated.id)
        assertEquals(newStart, updated.startDate)
        assertEquals(newEnd, updated.endDate)
    }

    @Test
    fun `update booking with non-existent id returns 404`() {
        val app = routes("/bookings/{id}" bind Method.PUT to bookingHandler::updateBooking)

        val request = Request(Method.PUT, "/bookings/999")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(UpdateBookingRequest(
                LocalDate.now().plusDays(1).toString(),
                LocalDate.now().plusDays(5).toString()
            )))

        assertEquals(Status.NOT_FOUND, app(request).status)
    }

    @Test
    fun `update booking with invalid date format returns 400`() {
        val created = bookingService.createBooking(1, 1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(5))

        val app = routes("/bookings/{id}" bind Method.PUT to bookingHandler::updateBooking)

        val request = Request(Method.PUT, "/bookings/${created.id}")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(UpdateBookingRequest("not-a-date", "2026-08-15")))

        val response = app(request)

        assertEquals(Status.BAD_REQUEST, response.status)
        assertTrue(response.bodyString().contains("Invalid date format"))
    }

    @Test
    fun `update booking with overlapping dates returns 409`() {
        // house 1 already booked for days 10-15
        bookingService.createBooking(1, 1, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15))
        // second booking on same house for days 20-25
        val toUpdate = bookingService.createBooking(1, 1, LocalDate.now().plusDays(20), LocalDate.now().plusDays(25))

        val app = routes("/bookings/{id}" bind Method.PUT to bookingHandler::updateBooking)

        // try to move second booking into the occupied window
        val request = Request(Method.PUT, "/bookings/${toUpdate.id}")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(UpdateBookingRequest(
                LocalDate.now().plusDays(12).toString(),
                LocalDate.now().plusDays(18).toString()
            )))

        assertEquals(Status.BAD_REQUEST, app(request).status)
    }

    @Test
    fun `update booking with invalid id format returns 400`() {
        val app = routes("/bookings/{id}" bind Method.PUT to bookingHandler::updateBooking)

        val request = Request(Method.PUT, "/bookings/abc")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(UpdateBookingRequest(
                LocalDate.now().plusDays(1).toString(),
                LocalDate.now().plusDays(5).toString()
            )))

        assertEquals(Status.BAD_REQUEST, app(request).status)
    }

    @Test
    fun `update booking without token returns 401`() {
        val app = routes("/bookings/{id}" bind Method.PUT to bookingHandler::updateBooking)

        val request = Request(Method.PUT, "/bookings/1")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(
                UpdateBookingRequest(
                    LocalDate.now().plusDays(1).toString(),
                    LocalDate.now().plusDays(5).toString()
                )
            ))

        assertEquals(Status.UNAUTHORIZED, app(request).status)
    }

    /*
    ---------------------------------------------
    // getAvailableDays
    ---------------------------------------------
     */

    @Test
    fun `get available days returns 200 and correct days`() {
        val app = routes("/houses/{id}/availability" bind Method.GET to bookingHandler::getAvailableDays)

        val request = Request(Method.GET, "/houses/1/availability")
            .query("month", "2030-08")

        val response = app(request)

        assertEquals(Status.OK, response.status)
        val body = Json.decodeFromString<AvailableDaysResponse>(response.bodyString())
        assertEquals(31, body.availableDays.size) // August has 31 days
    }

    @Test
    fun `get available days excludes booked days`() {
        bookingService.createBooking(1, 1, LocalDate.of(2030, 8, 10), LocalDate.of(2030, 8, 15))

        val app = routes("/houses/{id}/availability" bind Method.GET to bookingHandler::getAvailableDays)

        val request = Request(Method.GET, "/houses/1/availability")
            .query("month", "2030-08")

        val response = app(request)

        assertEquals(Status.OK, response.status)
        val body = Json.decodeFromString<AvailableDaysResponse>(response.bodyString())
        assertFalse(body.availableDays.contains("2030-08-10"))
        assertFalse(body.availableDays.contains("2030-08-14"))
        assertTrue(body.availableDays.contains("2030-08-15"))
    }

    @Test
    fun `get available days with missing month parameter returns 400`() {
        val app = routes("/houses/{id}/availability" bind Method.GET to bookingHandler::getAvailableDays)

        val request = Request(Method.GET, "/houses/1/availability")

        assertEquals(Status.BAD_REQUEST, app(request).status)
    }

    @Test
    fun `get available days with invalid month format returns 400`() {
        val app = routes("/houses/{id}/availability" bind Method.GET to bookingHandler::getAvailableDays)

        val request = Request(Method.GET, "/houses/1/availability")
            .query("month", "08-2030")

        assertEquals(Status.BAD_REQUEST, app(request).status)
    }

    @Test
    fun `get available days with invalid house id format returns 400`() {
        val app = routes("/houses/{id}/availability" bind Method.GET to bookingHandler::getAvailableDays)

        val request = Request(Method.GET, "/houses/abc/availability")
            .query("month", "2030-08")

        assertEquals(Status.BAD_REQUEST, app(request).status)
    }

    @Test
    fun `get available days returns empty list for a past month`() {
        val app = routes("/houses/{id}/availability" bind Method.GET to bookingHandler::getAvailableDays)

        val request = Request(Method.GET, "/houses/1/availability")
            .query("month", "2020-01")

        val response = app(request)

        assertEquals(Status.OK, response.status)
        val body = Json.decodeFromString<AvailableDaysResponse>(response.bodyString())
        assertTrue(body.availableDays.isEmpty())
    }
}