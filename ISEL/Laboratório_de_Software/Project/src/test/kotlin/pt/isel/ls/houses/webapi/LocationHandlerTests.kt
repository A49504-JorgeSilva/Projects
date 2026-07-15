package pt.isel.ls.houses.webapi

import kotlinx.serialization.json.Json
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.houses.data.mem.DbMem
import pt.isel.ls.houses.data.mem.LocationRepositoryMem
import pt.isel.ls.houses.data.mem.UserRepositoryMem
import pt.isel.ls.houses.domain.LocationType
import pt.isel.ls.houses.services.LocationServices
import pt.isel.ls.houses.services.UserServices
import pt.isel.ls.houses.webapi.dtos.CreateLocationRequest
import pt.isel.ls.houses.webapi.dtos.CreateLocationResponse
import pt.isel.ls.houses.webapi.dtos.LocationResponse
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

class LocationHandlerTests {
    private val userRepo = UserRepositoryMem()
    private val userService = UserServices(userRepo)

    private val locationRepo = LocationRepositoryMem()
    private val locationService = LocationServices(locationRepo)
    private val locationHandler = LocationHandler(locationService, userService)

    private lateinit var validToken: String

    @OptIn(ExperimentalUuidApi::class)
    @BeforeTest
    fun setUp() {
        DbMem.clearAll()

        val user = userService.createUser("Test User", "test@test.com", "alice123")
        validToken = user.token.toString()
    }

    // Helper para criar uma localização diretamente via serviço (útil para setup)
    private fun createLocation(name: String, type: LocationType, parentId: Int? = null): Int {
        return locationService.createLocation(name, type, parentId).id
    }

    @Test
    fun `create location returns 201 and id`() {
        val app = routes("/locations" bind Method.POST to locationHandler::createLocation)

        val request = Request(Method.POST, "/locations")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateLocationRequest("Portugal", LocationType.COUNTRY)))

        val response = app(request)
        assertEquals(Status.CREATED, response.status)

        val body = Json.decodeFromString<CreateLocationResponse>(response.bodyString())
        assertEquals(1, body.locationId)

        // Verificar que realmente foi guardado
        val saved = locationService.getLocationById(1)
        assertNotNull(saved)
        assertEquals("Portugal", saved.name)
        assertEquals(LocationType.COUNTRY, saved.type)
    }

    @Test
    fun `create location with invalid type returns 404`() {
        val app = routes("/locations" bind Method.POST to locationHandler::createLocation)

        // JSON com type inválido (não é um dos valores do enum)
        val invalidJson = """
            {"name": "Portugal", "type": "CONTINENT"}
        """.trimIndent()

        val request = Request(Method.POST, "/locations")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(invalidJson)

        val response = app(request)
        assertEquals(Status.NOT_FOUND, response.status)
        assertTrue(response.bodyString().contains("Location type not found"))
        assertTrue(response.bodyString().contains("COUNTRY, REGION, CITY, MUNICIPALITY, LOCALITY"))
    }

    @Test
    fun `create location with empty name returns 404`() {
        val app = routes("/locations" bind Method.POST to locationHandler::createLocation)

        val request = Request(Method.POST, "/locations")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateLocationRequest("", LocationType.CITY)))

        val response = app(request)
        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `create location with non existent parent returns 404`() {
        val app = routes("/locations" bind Method.POST to locationHandler::createLocation)

        val request = Request(Method.POST, "/locations")
            .header("Authorization", "Bearer $validToken")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateLocationRequest("Oeiras", LocationType.CITY, parentId = 999)))

        val response = app(request)
        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `createLocation without token should generate status code 401`() {
        val app = routes(
            "/locations" bind Method.POST to locationHandler::createLocation
        )

        val request = Request(Method.POST, "/locations")
            .header("Content-Type", "application/json")
            .body(Json.encodeToString(CreateLocationRequest("Portugal", LocationType.COUNTRY)))

        val response = app(request)
        assertEquals(Status.UNAUTHORIZED, response.status)
    }

    @Test
    fun `get location by id returns 200 and correct data`() {
        val createdId = createLocation("Portugal", LocationType.COUNTRY)

        val app = routes("/locations/{id}" bind Method.GET to locationHandler::getLocationById)

        val request = Request(Method.GET, "/locations/$createdId")
        val response = app(request)

        assertEquals(Status.OK, response.status)
        val location = Json.decodeFromString<LocationResponse>(response.bodyString())
        assertEquals(createdId, location.id)
        assertEquals("Portugal", location.name)
        assertEquals("COUNTRY", location.type)
        assertEquals(null, location.parentId)
    }

    @Test
    fun `get location by non existent id returns 404`() {
        val app = routes("/locations/{id}" bind Method.GET to locationHandler::getLocationById)

        val request = Request(Method.GET, "/locations/999")
        val response = app(request)

        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `get location with invalid id format returns 404`() {
        val app = routes("/locations/{id}" bind Method.GET to locationHandler::getLocationById)

        val request = Request(Method.GET, "/locations/abc")
        val response = app(request)

        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `get children of location returns 200 with children list`() {
        val parentId = createLocation("Pai", LocationType.COUNTRY)
        val child1 = createLocation("Filho1", LocationType.CITY, parentId)
        val child2 = createLocation("Filho2", LocationType.CITY, parentId)

        val app = routes("/locations/{id}/children" bind Method.GET to locationHandler::getChildrenLocations)

        val request = Request(Method.GET, "/locations/$parentId/children")
        val response = app(request)

        assertEquals(Status.OK, response.status)
        val children = Json.decodeFromString<List<LocationResponse>>(response.bodyString())
        assertEquals(2, children.size)
        assertEquals(child1, children[0].id)
        assertEquals(child2, children[1].id)
    }

    @Test
    fun `get children of location with no children returns empty list`() {
        val parentId = createLocation("Pai", LocationType.COUNTRY)

        val app = routes("/locations/{id}/children" bind Method.GET to locationHandler::getChildrenLocations)

        val request = Request(Method.GET, "/locations/$parentId/children")
        val response = app(request)

        assertEquals(Status.OK, response.status)
        val children = Json.decodeFromString<List<LocationResponse>>(response.bodyString())
        assertTrue(children.isEmpty())
    }

    @Test
    fun `get children of non existent location returns 404`() {
        val app = routes("/locations/{id}/children" bind Method.GET to locationHandler::getChildrenLocations)

        val request = Request(Method.GET, "/locations/999/children")
        val response = app(request)

        assertEquals(Status.NOT_FOUND, response.status)
    }

    @Test
    fun `get hierarchy path returns full path`() {
        val country = createLocation("Portugal", LocationType.COUNTRY)
        val region = createLocation("Lisboa", LocationType.REGION, country)
        val city = createLocation("Oeiras", LocationType.CITY, region)

        val app = routes("/locations/{id}/path" bind Method.GET to locationHandler::getHierarchyPath)

        val request = Request(Method.GET, "/locations/$city/path")
        val response = app(request)

        assertEquals(Status.OK, response.status)
        val path = Json.decodeFromString<List<LocationResponse>>(response.bodyString())
        assertEquals(3, path.size)
        assertEquals(country, path[0].id)
        assertEquals(region, path[1].id)
        assertEquals(city, path[2].id)
    }

    @Test
    fun `get hierarchy path of root returns list with itself`() {
        val rootId = createLocation("Portugal", LocationType.COUNTRY)

        val app = routes("/locations/{id}/path" bind Method.GET to locationHandler::getHierarchyPath)

        val request = Request(Method.GET, "/locations/$rootId/path")
        val response = app(request)

        assertEquals(Status.OK, response.status)
        val path = Json.decodeFromString<List<LocationResponse>>(response.bodyString())
        assertEquals(1, path.size)
        assertEquals(rootId, path[0].id)
    }

    @Test
    fun `get hierarchy path of non existent location returns 404`() {
        val app = routes("/locations/{id}/path" bind Method.GET to locationHandler::getHierarchyPath)

        val request = Request(Method.GET, "/locations/999/path")
        val response = app(request)

        assertEquals(Status.NOT_FOUND, response.status)
    }


    @Test
    fun `list locations returns 200 and all locations`() {
        createLocation("Portugal", LocationType.COUNTRY)
        createLocation("Spain", LocationType.COUNTRY)

        val app = routes("/locations" bind Method.GET to locationHandler::listLocations)

        val response = app(Request(Method.GET, "/locations"))

        assertEquals(Status.OK, response.status)
        val locations = Json.decodeFromString<List<LocationResponse>>(response.bodyString())
        assertEquals(2, locations.size)
    }

    @Test
    fun `list locations filtered by type returns only matching locations`() {
        val country = createLocation("Portugal", LocationType.COUNTRY)
        createLocation("Norte", LocationType.REGION, country)
        createLocation("Sul", LocationType.REGION, country)

        val app = routes("/locations" bind Method.GET to locationHandler::listLocations)

        val response = app(Request(Method.GET, "/locations").query("type", "REGION"))

        assertEquals(Status.OK, response.status)
        val locations = Json.decodeFromString<List<LocationResponse>>(response.bodyString())
        assertEquals(2, locations.size)
        assertTrue(locations.all { it.type == "REGION" })
    }

    @Test
    fun `list locations with invalid type returns 400`() {
        val app = routes("/locations" bind Method.GET to locationHandler::listLocations)

        val response = app(Request(Method.GET, "/locations").query("type", "CONTINENT"))

        assertEquals(Status.BAD_REQUEST, response.status)
    }

    @Test
    fun `list locations returns empty list when no locations exist`() {
        val app = routes("/locations" bind Method.GET to locationHandler::listLocations)

        val response = app(Request(Method.GET, "/locations"))

        assertEquals(Status.OK, response.status)
        val locations = Json.decodeFromString<List<LocationResponse>>(response.bodyString())
        assertTrue(locations.isEmpty())
    }
}