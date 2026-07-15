package pt.isel.ls.houses.webapi

import pt.isel.ls.houses.services.HouseServices
import org.http4k.core.Request
import org.http4k.core.Response
import kotlinx.serialization.json.Json
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.routing.bind
import org.http4k.routing.path
import pt.isel.ls.houses.services.UserServices
import pt.isel.ls.houses.webapi.dtos.CreateHouseRequest
import pt.isel.ls.houses.webapi.dtos.CreateHouseResponse
import pt.isel.ls.houses.webapi.dtos.GetHouseResponse
import pt.isel.ls.houses.webapi.dtos.GetHousesResponse

class HouseHandler (
    private val houseService: HouseServices,
    private val userService: UserServices,
) {
    fun createHouse(request: Request): Response = executeHandler(request) { req ->
        val user = getAuthenticatedUser(req, userService)
            ?: return@executeHandler Response(UNAUTHORIZED).body("Need to login or register")

        val body = Json.decodeFromString<CreateHouseRequest>(req.bodyString())
        val house = houseService.createNewHouse(
            title = body.title,
            locationId = body.locationId,
            area = body.areaSqMt,
            price = body.pricePerNight,
            description = body.description,
            ownerId = user.id
        )
        val response = CreateHouseResponse(
            id = house
        )
        Response(CREATED)
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(response))
    }


    fun getHouseById(request: Request): Response = executeHandler(request) { req ->
        val id = req.path("id")?.toIntOrNull()
            ?: throw IllegalArgumentException("Invalid id format")

        val house = houseService.getHouse(id)
        val response = GetHouseResponse(
            id = house.id,
            title = house.title,
            locationId = house.locationId,
            areaSqMt = house.areaSqMt,
            pricePerNight = house.pricePerNight,
            description = house.description,
            ownerId = house.ownerId
        )
        Response(OK)
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(response))
    }

    fun getAllHouses(request: Request): Response = executeHandler(request) { req ->
        val skip = req.query("skip")?.toIntOrNull() ?: 0
        val limit = req.query("limit")?.toIntOrNull() ?: 10
        val searchTitle = req.query("searchTitle")?.takeIf { it.isNotBlank() }
        val searchLocation = req.query("searchLocation")?.takeIf { it.isNotBlank() }

        val houses = houseService.listHouses(searchTitle, searchLocation, skip, limit)

        val response = GetHousesResponse(
            houses = houses.map { house ->
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

    fun routes() = org.http4k.routing.routes(
        "/houses" bind POST to ::createHouse,
        "/houses/{id}" bind GET to ::getHouseById,
        "/houses" bind GET to ::getAllHouses
    )
}