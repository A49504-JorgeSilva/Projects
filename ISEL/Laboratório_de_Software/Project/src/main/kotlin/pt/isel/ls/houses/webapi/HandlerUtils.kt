package pt.isel.ls.houses.webapi

import kotlinx.serialization.SerializationException
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.Status.Companion.UNPROCESSABLE_ENTITY
import pt.isel.ls.houses.domain.AuthenticationException
import java.time.format.DateTimeParseException
import pt.isel.ls.houses.domain.ConflictException
import pt.isel.ls.houses.domain.InvalidHierarchyException
import pt.isel.ls.houses.domain.InvalidInputException
import pt.isel.ls.houses.domain.NotFoundException
import pt.isel.ls.houses.domain.User
import pt.isel.ls.houses.services.UserServices
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Wraps a handler block and maps domain exceptions to the correct HTTP responses,
 * avoiding repetitive try-catch in every handler method.
 */
fun executeHandler(request: Request, handler: (Request) -> Response): Response = try {
    handler(request)
} catch (e: NotFoundException) {
    Response(NOT_FOUND).body(e.message ?: "Not found")
} catch (e: ConflictException) {
    Response(BAD_REQUEST).body(e.message ?: "Conflict")
} catch (e: InvalidInputException) {
    Response(NOT_FOUND).body(e.message ?: "Invalid input")
} catch (e: InvalidHierarchyException) {
    Response(UNPROCESSABLE_ENTITY).body(e.message ?: "Invalid hierarchy")
} catch (e: IllegalStateException) {
    Response(BAD_REQUEST).body(e.message ?: "Conflict")
} catch (e: IllegalArgumentException) {
    Response(BAD_REQUEST).body(e.message ?: "Bad request")
} catch (e: AuthenticationException) {
    Response(UNAUTHORIZED).body(e.message ?: "Invalid credentials")
} catch (_: DateTimeParseException) {
    Response(BAD_REQUEST).body("Invalid date format. Use YYYY-MM-DD")
} catch (e: SerializationException) {
    Response(BAD_REQUEST).body("Invalid request body: ${e.message}")
}

/**
 * Extracts and validates the Bearer token from the Authorization header.
 * Returns the authenticated User, or null if the token is missing or invalid.
 */
@OptIn(ExperimentalUuidApi::class)
fun getAuthenticatedUser(request: Request, userService: UserServices): User? {//we're gonna reuse it multiple times so its better to declare it in a helper file
    val authHeader = request.header("Authorization") ?: return null
    if (!authHeader.startsWith("Bearer ")) return null

    val tokenString = authHeader.removePrefix("Bearer ")
    val token = try {
        Uuid.parse(tokenString)
    } catch (_: Exception) {
        return null
    }

    return userService.getUserByToken(token)
}