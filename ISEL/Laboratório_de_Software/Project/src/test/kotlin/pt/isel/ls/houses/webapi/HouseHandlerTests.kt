package pt.isel.ls.houses.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.houses.data.mem.DbMem
import pt.isel.ls.houses.data.mem.HouseRepositoryMem
import pt.isel.ls.houses.data.mem.LocationRepositoryMem
import pt.isel.ls.houses.data.mem.UserRepositoryMem
import pt.isel.ls.houses.domain.LocationType
import pt.isel.ls.houses.services.HouseServices
import pt.isel.ls.houses.services.UserServices
import pt.isel.ls.houses.webapi.dtos.CreateHouseRequest
import pt.isel.ls.houses.webapi.dtos.CreateHouseResponse
import pt.isel.ls.houses.webapi.dtos.GetHouseResponse
import pt.isel.ls.houses.webapi.dtos.GetHousesResponse
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi

class HouseHandlerTests {
    private val userRepo = UserRepositoryMem()
    private val locationRepo = LocationRepositoryMem()
    private val userService = UserServices(userRepo)

    private var houseService = HouseServices(HouseRepositoryMem(), locationRepo)
    private var houseHandler = HouseHandler(houseService, userService)

    private lateinit var validToken: String
    private lateinit var userId: String

    @OptIn(ExperimentalUuidApi::class)
    @BeforeTest
    fun setup() {
        DbMem.clearAll()

        val user = userService.createUser("Test User", "test@test.com", "alice123")
        validToken = user.token.toString()
        userId = user.id.toString()
        repeat(4) { locationRepo.create("Locality ${it + 1}", LocationType.LOCALITY, null) }
    }

    @Test
    fun `createHouse with correct parameters should generate status code 201`() {
        val app =
            routes(
                "/houses" bind Method.POST to houseHandler::createHouse,
                "/houses/{id}" bind Method.GET to houseHandler::getHouseById,
                "/houses" bind Method.GET to houseHandler::getAllHouses
            )
        val requestHouse1 = Request.Companion(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(
                Json.encodeToString(
                    CreateHouseRequest(
                        "House 1",
                        1,
                        100.0,
                        50.0,
                        "A nice house"
                    )
            ))
        val responseHouse1 = app(requestHouse1)
        assertEquals(responseHouse1.status, Status.CREATED)
        val uutHouse1 = Json.decodeFromString<CreateHouseResponse>(responseHouse1.bodyString())
        assertEquals(1, uutHouse1.id)

        val requestHouse2 = Request.Companion(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(
                Json.encodeToString(
                    CreateHouseRequest(
                        "House 2",
                        2,
                        120.0,
                        75.0,
                        "A super nice house"
                    )
            ))
        val responseHouse2 = app(requestHouse2)
        assertEquals(responseHouse2.status, Status.CREATED)
        val uutHouse2 = Json.decodeFromString<CreateHouseResponse>(responseHouse2.bodyString())
        assertEquals(2, uutHouse2.id)
    }

    @Test
    fun `createHouse with incorrect parameters should generate status code 400 and 404`() {
        val app =
            routes(
                "/houses" bind Method.POST to houseHandler::createHouse,
                "/houses/{id}" bind Method.GET to houseHandler::getHouseById,
                "/houses" bind Method.GET to houseHandler::getAllHouses
            )
        val requestHouse1 = Request.Companion(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(
                Json.encodeToString(
                    CreateHouseRequest(
                        "   ",
                        1,
                        100.0,
                        50.0,
                        "A nice house"
                    )
            ))
        val responseHouse1 = app(requestHouse1)
        assertEquals(responseHouse1.status, Status.BAD_REQUEST)

        val requestHouse2 = Request.Companion(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(
                Json.encodeToString(
                    CreateHouseRequest(
                        "House 2",
                        -1,
                        100.0,
                        50.0,
                        "A nice house"
                    )
            ))
        val responseHouse2 = app(requestHouse2)
        assertEquals(responseHouse2.status, Status.NOT_FOUND)

        val requestHouse3 = Request.Companion(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(
                Json.encodeToString(
                    CreateHouseRequest(
                        "House 1",
                        1,
                        -100.0,
                        50.0,
                        "A nice house"
                    )
            ))
        val responseHouse3 = app(requestHouse3)
        assertEquals(responseHouse3.status, Status.BAD_REQUEST)

        val requestHouse4 = Request.Companion(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(
                Json.encodeToString(
                    CreateHouseRequest(
                        "House 1",
                        1,
                        100.0,
                        -50.0,
                        "A nice house"
                    )
            ))
        val responseHouse4 = app(requestHouse4)
        assertEquals(responseHouse4.status, Status.BAD_REQUEST)

        val requestHouse5 = Request.Companion(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(
                Json.encodeToString(
                    CreateHouseRequest(
                        "House 1",
                        1,
                        100.0,
                        50.0,
                        "   "
                    )
            ))
        val responseHouse5 = app(requestHouse5)
        assertEquals(responseHouse5.status, Status.BAD_REQUEST)
    }

    @Test
    fun `getHouse with correct parameters should generate status code 200 and correct data`() {
        val app =
            routes(
                "/houses" bind Method.POST to houseHandler::createHouse,
                "/houses/{id}" bind Method.GET to houseHandler::getHouseById,
                "/houses" bind Method.GET to houseHandler::getAllHouses
            )
        val requestPostHouse1 = Request.Companion(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(
                Json.encodeToString(
                    CreateHouseRequest(
                        "House 1",
                        1,
                        100.0,
                        50.0,
                        "A nice house"
                    )
            ))
        app(requestPostHouse1)

        val requestGetHouse1 = Request.Companion(Method.GET, "/houses/1")
        val responseGetHouse1 = app(requestGetHouse1)
        assertEquals(responseGetHouse1.status, Status.OK)
        val uutGetHouse1 = Json.decodeFromString<GetHouseResponse>(responseGetHouse1.bodyString())
        assertEquals(1, uutGetHouse1.id)
        assertEquals("House 1", uutGetHouse1.title)
        assertEquals(1, uutGetHouse1.locationId)
        assertEquals(100.0, uutGetHouse1.areaSqMt)
        assertEquals(50.0, uutGetHouse1.pricePerNight)
        assertEquals("A nice house", uutGetHouse1.description)
    }

    @Test
    fun `getHouse with invalid id should generate status code 400`() {
        val app =
            routes(
                "/houses" bind Method.POST to houseHandler::createHouse,
                "/houses/{id}" bind Method.GET to houseHandler::getHouseById,
                "/houses" bind Method.GET to houseHandler::getAllHouses
            )
        val requestGetHouse1 = Request.Companion(Method.GET, "/houses/abc")
        val responseGetHouse1 = app(requestGetHouse1)
        assertEquals(responseGetHouse1.status, Status.BAD_REQUEST)
    }

    @Test
    fun `getHouse with non-existent id should generate status code 404`() {
        val app =
            routes(
                "/houses" bind Method.POST to houseHandler::createHouse,
                "/houses/{id}" bind Method.GET to houseHandler::getHouseById,
                "/houses" bind Method.GET to houseHandler::getAllHouses
            )
        val requestGetHouse1 = Request.Companion(Method.GET, "/houses/999")
        val responseGetHouse1 = app(requestGetHouse1)
        assertEquals(responseGetHouse1.status, Status.NOT_FOUND)
    }

    @Test
    fun `getAllHouses with correct parameters should generate status code 200 and correct data`() {
        val app =
            routes(
                "/houses" bind Method.POST to houseHandler::createHouse,
                "/houses/{id}" bind Method.GET to houseHandler::getHouseById,
                "/houses" bind Method.GET to houseHandler::getAllHouses
            )
        val requestPostHouse1 = Request.Companion(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(
                Json.encodeToString(
                    CreateHouseRequest(
                        "House 1",
                        1,
                        100.0,
                        50.0,
                        "A nice house"
                    )
            ))
        app(requestPostHouse1)

        val requestPostHouse2 = Request.Companion(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(
                Json.encodeToString(
                    CreateHouseRequest(
                        "House 2",
                        2,
                        120.0,
                        75.0,
                        "A super nice house"
                    )
            ))
        app(requestPostHouse2)

        val requestGetAllHouses = Request.Companion(Method.GET, "/houses")
        val responseGetAllHouses = app(requestGetAllHouses)
        assertEquals(responseGetAllHouses.status, Status.OK)
        val uutGetAllHouses = Json.decodeFromString<GetHousesResponse>(responseGetAllHouses.bodyString())
        assertEquals(2, uutGetAllHouses.houses.size)
    }

    @Test
    fun `getAllHouses should generate status code 200 and empty list when no houses exist`() {
        val app =
            routes(
                "/houses" bind Method.POST to houseHandler::createHouse,
                "/houses/{id}" bind Method.GET to houseHandler::getHouseById,
                "/houses" bind Method.GET to houseHandler::getAllHouses
            )
        val requestGetAllHouses = Request.Companion(Method.GET, "/houses")
        val responseGetAllHouses = app(requestGetAllHouses)
        assertEquals(responseGetAllHouses.status, Status.OK)
        val uutGetAllHouses = Json.decodeFromString<GetHousesResponse>(responseGetAllHouses.bodyString())
        assertEquals(0, uutGetAllHouses.houses.size)
    }

    @Test
    fun `createHouse without token should generate status code 401`() {
        val app = routes(
            "/houses" bind Method.POST to houseHandler::createHouse
        )

        val request = Request(Method.POST, "/houses")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateHouseRequest("House 1", 1, 100.0, 50.0, "A nice house")))

        val response = app(request)
        assertEquals(Status.UNAUTHORIZED, response.status)
    }

    @Test
    fun `getAllHouses should filter by searchTitle`() {
        val app = routes(
            "/houses" bind Method.POST to houseHandler::createHouse,
            "/houses" bind Method.GET to houseHandler::getAllHouses
        )

        app(Request(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateHouseRequest("Beach House", 1, 100.0, 50.0, "Desc"))))
        app(Request(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateHouseRequest("Mountain Cabin", 1, 100.0, 50.0, "Desc"))))

        val response = app(Request(Method.GET, "/houses").query("searchTitle", "beach"))

        assertEquals(Status.OK, response.status)
        val body = Json.decodeFromString<GetHousesResponse>(response.bodyString())
        assertEquals(1, body.houses.size)
        assertEquals("Beach House", body.houses.first().title)
    }

    @Test
    fun `getAllHouses should filter by searchLocation`() {
        DbMem.locations[1] = pt.isel.ls.houses.domain.Location(1, "Lisbon", pt.isel.ls.houses.domain.LocationType.LOCALITY, null)
        DbMem.locations[2] = pt.isel.ls.houses.domain.Location(2, "Porto", pt.isel.ls.houses.domain.LocationType.LOCALITY, null)

        val app = routes(
            "/houses" bind Method.POST to houseHandler::createHouse,
            "/houses" bind Method.GET to houseHandler::getAllHouses
        )

        app(Request(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateHouseRequest("House A", 1, 100.0, 50.0, "Desc"))))
        app(Request(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateHouseRequest("House B", 2, 100.0, 50.0, "Desc"))))

        val response = app(Request(Method.GET, "/houses").query("searchLocation", "lisbon"))

        assertEquals(Status.OK, response.status)
        val body = Json.decodeFromString<GetHousesResponse>(response.bodyString())
        assertEquals(1, body.houses.size)
        assertEquals("House A", body.houses.first().title)
    }

    @Test
    fun `getAllHouses should filter by searchTitle AND searchLocation`() {
        DbMem.locations[1] = pt.isel.ls.houses.domain.Location(1, "Lisbon", pt.isel.ls.houses.domain.LocationType.LOCALITY, null)
        DbMem.locations[2] = pt.isel.ls.houses.domain.Location(2, "Porto", pt.isel.ls.houses.domain.LocationType.LOCALITY, null)

        val app = routes(
            "/houses" bind Method.POST to houseHandler::createHouse,
            "/houses" bind Method.GET to houseHandler::getAllHouses
        )

        app(Request(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateHouseRequest("Beach House", 1, 100.0, 50.0, "Desc"))))
        app(Request(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateHouseRequest("Beach House", 2, 100.0, 50.0, "Desc"))))
        app(Request(Method.POST, "/houses")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateHouseRequest("Mountain Cabin", 1, 100.0, 50.0, "Desc"))))

        val response = app(Request(Method.GET, "/houses")
            .query("searchTitle", "beach")
            .query("searchLocation", "lisbon"))

        assertEquals(Status.OK, response.status)
        val body = Json.decodeFromString<GetHousesResponse>(response.bodyString())
        assertEquals(1, body.houses.size)
        assertEquals(1, body.houses.first().locationId)
    }
}