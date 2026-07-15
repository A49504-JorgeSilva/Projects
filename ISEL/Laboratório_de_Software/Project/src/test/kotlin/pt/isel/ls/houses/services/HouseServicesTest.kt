package pt.isel.ls.houses.services

import pt.isel.ls.houses.data.mem.DbMem
import pt.isel.ls.houses.data.mem.HouseRepositoryMem
import pt.isel.ls.houses.data.mem.LocationRepositoryMem
import pt.isel.ls.houses.domain.InvalidInputException
import pt.isel.ls.houses.domain.Location
import pt.isel.ls.houses.domain.LocationType
import pt.isel.ls.houses.domain.NotFoundException
import kotlin.test.*

class HouseServicesTest {

    private var houseRepositoryMem = HouseRepositoryMem()
    private var locationRepo = LocationRepositoryMem()

    private var houseService = HouseServices(houseRepositoryMem, locationRepo)

    @BeforeTest
    fun setup() {
        DbMem.clearAll()
        repeat(3) { locationRepo.create("Locality ${it + 1}", LocationType.LOCALITY, null) }
    }

    @Test
    fun `createNewHouse should create a new house and return its id`() {
        // Arrange
        val title = "Cozy Cottage"
        val locationId = 1
        val area = 75.0
        val price = 150.0
        val description = "A cozy cottage in the countryside."
        val ownerId = 1

        // Act
        val newHouseId = houseService.createNewHouse(title, locationId, area, price, description, ownerId)
        val createdHouse = houseService.getHouse(newHouseId)

        // Assert
        assertEquals(1, newHouseId)
        assertEquals(title, createdHouse.title)
        assertEquals(locationId, createdHouse.locationId)
        assertEquals(area, createdHouse.areaSqMt)
        assertEquals(price, createdHouse.pricePerNight)
        assertEquals(description, createdHouse.description)
        assertEquals(ownerId, createdHouse.ownerId)
    }

    @Test
    fun `createNewHouse should throw IllegalArgumentException for invalid input`() {
        // Arrange
        val invalidTitle = ""
        val invalidLocationId = -1
        val invalidArea = -10.0
        val invalidPrice = -50.0
        val invalidDescription = ""
        val invalidOwnerId = -1

        // Act & Assert
        assertFailsWith<IllegalArgumentException> { houseService.createNewHouse(invalidTitle, 1, 75.0, 150.0, "Valid description", 1) }
        assertFailsWith<NotFoundException> { houseService.createNewHouse("Valid title", invalidLocationId, 75.0, 150.0, "Valid description", 1) }
        assertFailsWith<IllegalArgumentException> { houseService.createNewHouse("Valid title", 1, invalidArea, 150.0, "Valid description", 1) }
        assertFailsWith<IllegalArgumentException> { houseService.createNewHouse("Valid title", 1, 75.0, invalidPrice, "Valid description", 1) }
        assertFailsWith<IllegalArgumentException> { houseService.createNewHouse("Valid title", 1, 75.0, 150.0, invalidDescription, 1) }
        assertFailsWith<IllegalArgumentException> { houseService.createNewHouse("Valid title", 1, 75.0, 150.0, "Valid Description", invalidOwnerId) }
    }

    @Test
    fun `getHouse should throw NoSuchElementException for non-existent house id`() {
        // Arrange
        val nonExistentId = 999

        // Act & Assert
        assertFailsWith<NotFoundException> { houseService.getHouse(nonExistentId) }
    }

    @Test
    fun `listHouses should return a list of houses with pagination`() {
        // Arrange
        houseService.createNewHouse("House 1", 1, 50.0, 100.0, "Description 1", 1)
        houseService.createNewHouse("House 2", 2, 60.0, 120.0, "Description 2", 2)
        houseService.createNewHouse("House 3", 3, 70.0, 140.0, "Description 3", 3)

        // Act
        val firstPage = houseService.listHouses(skip = 0, limit = 2)
        val secondPage = houseService.listHouses(skip = 2, limit = 2)

        // Assert
        assertEquals(2, firstPage.size)
        assertEquals("House 1", firstPage[0].title)
        assertEquals("House 2", firstPage[1].title)

        assertEquals(1, secondPage.size)
        assertEquals("House 3", secondPage[0].title)
    }

    @Test
    fun `listHouses should throw IllegalArgumentException for invalid pagination parameters`() {
        // Act & Assert
        assertFailsWith<IllegalArgumentException> { houseService.listHouses(skip = -1, limit = 10) }
        assertFailsWith<IllegalArgumentException> { houseService.listHouses(skip = 0, limit = -5) }
    }

    @Test
    fun `listHouses should filter houses by searchTitle`() {
        // Arrange
        houseService.createNewHouse("Beach House", 1, 50.0, 100.0, "Desc1", 1)
        houseService.createNewHouse("Mountain Cabin", 2, 60.0, 120.0, "Desc2", 2)
        houseService.createNewHouse("Beach Apartment", 3, 70.0, 140.0, "Desc3", 3)

        // Act
        val result = houseService.listHouses(searchTitle  = "beach", skip = 0, limit = 10)

        // Assert
        assertEquals(2, result.size)
        assertTrue(result.all { it.title.contains("Beach", ignoreCase = true) })
    }


    @Test
    fun `listHouses should ignore empty search filter`() {
        // Arrange
        houseService.createNewHouse("Beach House", 1, 50.0, 100.0, "Desc1", 1)
        houseService.createNewHouse("Mountain Cabin", 2, 60.0, 120.0, "Desc2", 2)

        // Act - teste que null ou empty search retorna todas as casas
        val result = houseService.listHouses(searchTitle = null, searchLocation = null, skip = 0, limit = 10)

        // Assert
        assertEquals(2, result.size) // deve retornar todas, sem filtro
    }

    @Test
    fun `listHouses should filter houses by searchLocation`() {
        DbMem.locations[1] = Location(id = 1, name = "Lisbon", type = LocationType.LOCALITY, parentId = null)
        DbMem.locations[2] = Location(id = 2, name = "Porto", type = LocationType.LOCALITY, parentId = null)

        houseService.createNewHouse("House A", 1, 50.0, 100.0, "Desc", 1)
        houseService.createNewHouse("House B", 2, 60.0, 120.0, "Desc", 1)

        val result = houseService.listHouses(searchLocation = "lisbon", skip = 0, limit = 10)

        assertEquals(1, result.size)
        assertEquals("House A", result.first().title)
    }

    @Test
    fun `listHouses should filter by searchTitle AND searchLocation`() {
        DbMem.locations[1] = Location(id = 1, name = "Lisbon", type = LocationType.LOCALITY, parentId = null)
        DbMem.locations[2] = Location(id = 2, name = "Porto", type = LocationType.LOCALITY, parentId = null)

        houseService.createNewHouse("Beach House", 1, 50.0, 100.0, "Desc", 1) // title matches, location matches
        houseService.createNewHouse("Beach House", 2, 60.0, 120.0, "Desc", 1) // title matches, location doesn't
        houseService.createNewHouse("Mountain Cabin", 1, 70.0, 140.0, "Desc", 1) // title doesn't, location matches

        val result = houseService.listHouses(
            searchTitle = "beach",
            searchLocation = "lisbon",
            skip = 0,
            limit = 10
        )

        assertEquals(1, result.size)
        assertEquals("Beach House", result.first().title)
        assertEquals(1, result.first().locationId)
    }

    @Test
    fun `createNewHouse throws InvalidInputException if location is not a LOCALITY`() {
        val country = locationRepo.create("Portugal", LocationType.COUNTRY, null)

        assertFailsWith<InvalidInputException> {
            houseService.createNewHouse("Some House", country.id, 50.0, 100.0, "Desc", 1)
        }
    }

    @Test
    fun `createNewHouse throws NotFoundException if location does not exist`() {
        assertFailsWith<NotFoundException> {
            houseService.createNewHouse("Some House", 999, 50.0, 100.0, "Desc", 1)
        }
    }
}