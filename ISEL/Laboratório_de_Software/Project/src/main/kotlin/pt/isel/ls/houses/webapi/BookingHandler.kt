package pt.isel.ls.houses.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Method.DELETE
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import pt.isel.ls.houses.domain.*
import pt.isel.ls.houses.services.BookingServices
import pt.isel.ls.houses.services.UserServices
import pt.isel.ls.houses.webapi.dtos.*
import java.time.LocalDate
import java.time.YearMonth
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class BookingHandler(
    private val bookingService: BookingServices,
    private val userService: UserServices
) {

    fun createBooking(request: Request): Response =
        executeHandler(request) { req ->
            val user = getAuthenticatedUser(req, userService)
                ?: return@executeHandler Response(UNAUTHORIZED).body("Need to login or register")

            val body = Json.decodeFromString<CreateBookingRequest>(req.bodyString())

            val start = LocalDate.parse(body.startDate)
            val end = LocalDate.parse(body.endDate)

            val booking = bookingService.createBooking(body.hid, user.id, start, end)

            val response = CreateBookingResponse(id = booking.id)
            Response(CREATED)
                .header("Content-Type", "application/json")
                .body(Json.encodeToString(response))
        }

    fun getBookingById(request: Request): Response =
        executeHandler(request) { req ->
            getAuthenticatedUser(req, userService)
                ?: return@executeHandler Response(UNAUTHORIZED).body("Need to login or register")

            val id = req.path("id")?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid id format")

            val booking = bookingService.getBookingById(id)

            Response(OK)
                .header("Content-Type", "application/json")
                .body(Json.encodeToString(booking.toResponse()))
        }

    fun listBookings(request: Request): Response =
        executeHandler(request) { req ->
            getAuthenticatedUser(req, userService)
                ?: return@executeHandler Response(UNAUTHORIZED).body("Need to login or register")

            val hid = req.query("hid")?.toIntOrNull()
                ?: throw IllegalArgumentException("Missing or invalid hid parameter")

            val startDateStr = req.query("startDate")
            val endDateStr = req.query("endDate")

            val skip = req.query("skip")?.toIntOrNull() ?: 0
            val limit = req.query("limit")?.toIntOrNull() ?: 10

            val start = if (startDateStr != null) LocalDate.parse(startDateStr) else LocalDate.of(2000, 1, 1)
            val end = if (endDateStr != null) LocalDate.parse(endDateStr) else LocalDate.of(2100,12, 31)

            val bookings = bookingService.listBookings(hid, start, end, skip, limit)

            Response(OK)
                .header("Content-Type", "application/json")
                .body(Json.encodeToString(bookings.map { it.toResponse() }))
        }

    fun getAvailableHouses(request: Request): Response =
        executeHandler(request) { req ->
            val startDateStr = req.query("startDate")
                ?: throw IllegalArgumentException("Missing startDate parameter")
            val endDateStr = req.query("endDate")
                ?: throw IllegalArgumentException("Missing endDate parameter")

            val skip = req.query("skip")?.toIntOrNull() ?: 0
            val limit = req.query("limit")?.toIntOrNull() ?: 10

            val start = LocalDate.parse(startDateStr)
            val end = LocalDate.parse(endDateStr)

            val availableHouses = bookingService.getAvailableHouses(start, end, skip, limit)

            val response = GetHousesResponse(
                houses = availableHouses.map { house ->
                    GetHouseResponse(
                        id = house.id,
                        title = house.title,
                        locationId = house.locationId,
                        areaSqMt = house.areaSqMt,
                        pricePerNight = house.pricePerNight,
                        description = house.description,
                        ownerId = house.ownerId
                    )
                }
            )

            Response(OK)
                .header("Content-Type", "application/json")
                .body(Json.encodeToString(response))
        }

    fun deleteBooking(request: Request): Response = executeHandler(request) { req ->
        getAuthenticatedUser(req, userService)
            ?: return@executeHandler Response(UNAUTHORIZED).body("Need to login or register")

        val id = req.path("id")?.toIntOrNull()
            ?: throw IllegalArgumentException("Invalid id format")

        bookingService.deleteBooking(id)

        Response(NO_CONTENT)
    }

    fun updateBooking(request: Request): Response = executeHandler(request) { req ->
        getAuthenticatedUser(req, userService)
            ?: return@executeHandler Response(UNAUTHORIZED).body("Need to login or register")

        val id = req.path("id")?.toIntOrNull()
            ?: throw IllegalArgumentException("Invalid id format")

        val body = Json.decodeFromString<UpdateBookingRequest>(req.bodyString())

        val start = LocalDate.parse(body.startDate)
        val end = LocalDate.parse(body.endDate)

        val updated = bookingService.updateBooking(id, start, end)

        Response(OK)
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(updated.toResponse()))
    }

    fun getAvailableDays(request: Request): Response =executeHandler(request) { req ->
        val hid = req.path("id")?.toIntOrNull()
            ?: throw IllegalArgumentException("Invalid id format")
        val monthStr = req.query("month")
            ?: throw IllegalArgumentException("Missing month parameter")

        val yearMonth = YearMonth.parse(monthStr)

        val availableDays = bookingService.getAvailableDaysInMonth(
            hid,
            yearMonth.year,
            yearMonth.monthValue
        )

        val response = AvailableDaysResponse(availableDays = availableDays.map { it.toString() })

        Response(OK)
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(response))
    }

    fun routes() = routes(
        "/bookings" bind POST to ::createBooking,
        "/bookings/available" bind GET to ::getAvailableHouses,
        "/bookings/{id}" bind GET to ::getBookingById,
        "/bookings/{id}" bind DELETE to ::deleteBooking,
        "/bookings/{id}" bind PUT to ::updateBooking,
        "/bookings" bind GET to ::listBookings,
        "/houses/{id}/availability" bind GET to ::getAvailableDays,
        "/houses/{id}/reserved" bind GET to ::listBookings,
    )

    private fun Booking.toResponse() = BookingResponse(
        id = this.id,
        startDate = this.startDate.toString(),
        endDate = this.endDate.toString(),
        userId = this.userId,
        houseId = this.houseId
    )
}
