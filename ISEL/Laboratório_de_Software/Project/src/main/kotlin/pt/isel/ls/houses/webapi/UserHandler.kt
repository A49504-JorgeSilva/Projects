package pt.isel.ls.houses.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.routing.routes
import org.http4k.routing.bind
import org.http4k.routing.path
import pt.isel.ls.houses.domain.Booking
import pt.isel.ls.houses.domain.ConflictException
import pt.isel.ls.houses.domain.InvalidInputException
import pt.isel.ls.houses.domain.NotFoundException
import pt.isel.ls.houses.services.BookingServices
import pt.isel.ls.houses.services.UserServices
import pt.isel.ls.houses.webapi.dtos.BookingResponse
import pt.isel.ls.houses.webapi.dtos.CreateUserRequest
import pt.isel.ls.houses.webapi.dtos.CreateUserResponse
import pt.isel.ls.houses.webapi.dtos.GetUserResponse
import pt.isel.ls.houses.webapi.dtos.LoginRequest
import pt.isel.ls.houses.webapi.dtos.LoginResponse
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class UserHandler(private val userService: UserServices, private val bookingService: BookingServices) {
    fun createUser(request: Request): Response =
        executeHandler(request) { req ->
            val body = Json.decodeFromString<CreateUserRequest>(req.bodyString())
            val user = userService.createUser(body.name, body.email, body.password)
            val response = CreateUserResponse(
                id = user.id,
                token = user.token.toString()
            )
            Response(CREATED)
                .header("Content-Type", "application/json")
                .body(Json.encodeToString(response))

        }

    fun getUserById(request: Request): Response =
        executeHandler(request) { req ->
            val id = req.path("id")?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid id format")

            val user = getAuthenticatedUser(req, userService)
                ?: return@executeHandler Response(UNAUTHORIZED).body("Need to login or register")

            val foundUser = userService.getUserById(id)
            val response = GetUserResponse(
                id = foundUser.id,
                name = foundUser.name,
                email = foundUser.email
            )
            Response(OK)
                .header("Content-Type", "application/json")
                .body(Json.encodeToString(response))
        }

    fun listBookingsByUser(request: Request): Response =
        executeHandler(request) { req ->
            val user = getAuthenticatedUser(req, userService)
                ?: return@executeHandler Response(UNAUTHORIZED).body("Need to login or register")

            val userId = req.path("id")?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid user id format")

            userService.getUserById(userId)

            val skip = req.query("skip")?.toIntOrNull() ?: 0
            val limit = req.query("limit")?.toIntOrNull() ?: 10

            val bookings = bookingService.listBookingsByUser(userId, skip, limit)

            Response(OK)
                .header("Content-Type", "application/json")
                .body(Json.encodeToString(bookings.map { it.toBookingResponse() }))
        }

    fun login(request: Request): Response = executeHandler(request) { req ->
        val body = Json.decodeFromString<LoginRequest>(req.bodyString())

        val user = userService.login(body.email, body.password)
        val response = LoginResponse(id = user.id, token = user.token.toString())

        Response(OK).header("Content-Type", "application/json").body(Json.encodeToString(response))
    }

    fun routes() = routes(
        "/users" bind POST to ::createUser,
        "/users/{id}" bind GET to ::getUserById,
        "/login" bind POST to ::login,
        "/users/{id}/bookings" bind GET  to ::listBookingsByUser
    )

    private fun Booking.toBookingResponse()= BookingResponse(
        id = this.id,
        startDate = this.startDate.toString(),
        endDate = this.endDate.toString(),
        userId = this.userId,
        houseId = this.houseId
    )
}