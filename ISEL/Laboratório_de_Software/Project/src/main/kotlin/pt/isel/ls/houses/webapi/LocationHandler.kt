package pt.isel.ls.houses.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNPROCESSABLE_ENTITY
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.routing.routes
import org.http4k.routing.bind
import org.http4k.routing.path
import pt.isel.ls.houses.domain.*
import pt.isel.ls.houses.services.LocationServices
import pt.isel.ls.houses.services.UserServices
import pt.isel.ls.houses.webapi.dtos.CreateLocationRequest
import pt.isel.ls.houses.webapi.dtos.CreateLocationResponse
import pt.isel.ls.houses.webapi.dtos.LocationResponse

class LocationHandler (
    private val locationServices: LocationServices,
    private val userServices: UserServices,
) {
    fun createLocation(request: Request): Response=
        executeHandler(request) { req ->
            getAuthenticatedUser(req, userServices)
                ?: return@executeHandler Response(UNAUTHORIZED).body("Need to login or register")

            val body = try {
                Json.decodeFromString<CreateLocationRequest>(req.bodyString())
            } catch (e: Exception) {
                // Checking the serialization message lets us return a friendlier error when an unknown enum value is sent
                if (e.message?.contains("LocationType") == true) {
                    throw NotFoundException(
                        "Location type not found. Must be one of: ${LocationType.entries.joinToString()}"
                    )
                } else {
                    throw InvalidInputException("Invalid request body")
                }
            }
            val location = locationServices.createLocation(body.name, body.type, body.parentId)
            val response = CreateLocationResponse(locationId = location.id)

            Response(CREATED)
                .header("Content-Type", "application/json")
                .body(Json.encodeToString(response))
        }

    fun getLocationById(request: Request): Response =
        executeHandler(request) { req ->
            val id = req.path("id")?.toIntOrNull()
                ?: throw NotFoundException("Input id not found")

            val location = locationServices.getLocationById(id)

            Response(OK)
                .header("Content-Type", "application/json")
                .body(Json.encodeToString(location.toResponse()))
    }

    fun getChildrenLocations(request: Request): Response =
        executeHandler(request) { req ->
            val id = req.path("id")?.toIntOrNull()
                ?: throw NotFoundException("Input id not found")

            val children = locationServices.getChildrenLocations(id)

            Response(OK)
                .header("Content-Type", "application/json")
                .body(Json.encodeToString(children.map { it.toResponse() }))
        }

    fun getHierarchyPath(request: Request): Response =
        executeHandler(request) { req ->
            val id = req.path("id")?.toIntOrNull()
                ?: throw NotFoundException("Input id not found")

            val path = locationServices.getHierarchyPath(id)

            Response(OK)
                .header("Content-Type", "application/json")
                .body(Json.encodeToString(path.map { it.toResponse() }))
        }

    fun listLocations(request: Request): Response = executeHandler(request) { req ->
        val nameQuery = req.query("name")
        if (nameQuery != null) {
            val locations = locationServices.listLocationsByPartialName(nameQuery)
            return@executeHandler Response(OK)
                .header("Content-Type", "application/json")
                .body(Json.encodeToString(locations.map { it.toResponse() }))
        }

        val typeStr = req.query("type")
        val type = if (typeStr != null) {
            try {
                LocationType.valueOf(typeStr.uppercase())
            } catch (_: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid type. Must be one of: ${LocationType.entries.joinToString()}")
            }
        } else null

        val locations = locationServices.listLocations(type)
        Response(OK)
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(locations.map { it.toResponse() }))
    }

    fun routes() = routes(
        "/locations" bind POST to ::createLocation,
        "/locations" bind GET to ::listLocations,
        "/locations/{id}" bind GET  to ::getLocationById,
        "/locations/{id}/children" bind GET  to ::getChildrenLocations,
        "/locations/{id}/path" bind GET  to ::getHierarchyPath,
    )

    private fun Location.toResponse() = LocationResponse(
        id = this.id,
        name = this.name,
        type = this.type.name,
        parentId = this.parentId
    )
}