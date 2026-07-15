package pt.isel.ls.houses.services

import pt.isel.ls.houses.data.mem.DbMem
import pt.isel.ls.houses.data.mem.LocationRepositoryMem
import pt.isel.ls.houses.domain.*
import kotlin.test.*

class LocationServiceTest {

    private lateinit var repository: LocationRepositoryMem
    private lateinit var service: LocationServices

    @BeforeTest
    fun setup() {
        DbMem.clearAll()
        repository = LocationRepositoryMem()
        service = LocationServices(repository)
    }

    // -------------------------------------------------------------------------
    // createLocation
    // -------------------------------------------------------------------------

    @Test
    fun `createLocation returns location with correct fields`() {
        val country = service.createLocation("Portugal", LocationType.COUNTRY)

        assertEquals("Portugal", country.name)
        assertEquals(LocationType.COUNTRY, country.type)
        assertNull(country.parentId)
        assertEquals(1, country.id)
    }

    @Test
    fun `createLocation increments id for each new location`() {
        val country = service.createLocation("Portugal", LocationType.COUNTRY)
        val region = service.createLocation("Norte", LocationType.REGION, country.id)

        assertEquals(1, country.id)
        assertEquals(2, region.id)
    }

    @Test
    fun `createLocation trims whitespace from name`() {
        val country = service.createLocation("  Portugal  ", LocationType.COUNTRY)

        assertEquals("Portugal", country.name)
    }

    @Test
    fun `createLocation with blank name throws InvalidInputException`() {
        assertFailsWith<InvalidInputException> {
            service.createLocation("   ", LocationType.COUNTRY)
        }
    }

    @Test
    fun `createLocation non-COUNTRY without parent throws InvalidInputException`() {
        assertFailsWith<InvalidInputException> {
            service.createLocation("Norte", LocationType.REGION)
        }
    }

    @Test
    fun `createLocation with non-existent parentId throws NotFoundException`() {
        assertFailsWith<NotFoundException> {
            service.createLocation("Norte", LocationType.REGION, parentId = 99)
        }
    }

    @Test
    fun `createLocation with invalid hierarchy throws InvalidHierarchyException`() {
        val country = service.createLocation("Portugal", LocationType.COUNTRY)
        val region = service.createLocation("Norte", LocationType.REGION, country.id)

        // COUNTRY cannot be child of REGION
        assertFailsWith<InvalidHierarchyException> {
            service.createLocation("SomeCountry", LocationType.COUNTRY, region.id)
        }
    }

    @Test
    fun `createLocation with same type as parent throws InvalidHierarchyException`() {
        val country = service.createLocation("Portugal", LocationType.COUNTRY)

        assertFailsWith<InvalidHierarchyException> {
            service.createLocation("Espanha", LocationType.COUNTRY, country.id)
        }
    }

    @Test
    fun `createLocation with duplicate name under same parent throws ConflictException`() {
        val country = service.createLocation("Portugal", LocationType.COUNTRY)
        service.createLocation("Norte", LocationType.REGION, country.id)

        assertFailsWith<ConflictException> {
            service.createLocation("Norte", LocationType.REGION, country.id)
        }
    }

    @Test
    fun `createLocation allows same name under different parents`() {
        val portugal = service.createLocation("Portugal", LocationType.COUNTRY)
        val espanha = service.createLocation("Espanha", LocationType.COUNTRY)

        // "Norte" exists under both countries — should be allowed
        service.createLocation("Norte", LocationType.REGION, portugal.id)
        val norte2 = service.createLocation("Norte", LocationType.REGION, espanha.id)

        assertEquals("Norte", norte2.name)
        assertEquals(espanha.id, norte2.parentId)
    }

    @Test
    fun `createLocation duplicate name check is case-insensitive`() {
        val country = service.createLocation("Portugal", LocationType.COUNTRY)
        service.createLocation("Norte", LocationType.REGION, country.id)

        assertFailsWith<ConflictException> {
            service.createLocation("norte", LocationType.REGION, country.id)
        }
    }

    @Test
    fun `createLocation full hierarchy chain succeeds`() {
        val country      = service.createLocation("Portugal",  LocationType.COUNTRY)
        val region       = service.createLocation("Norte",     LocationType.REGION,       country.id)
        val city         = service.createLocation("Porto",     LocationType.CITY,         region.id)
        val municipality = service.createLocation("Matosinhos",LocationType.MUNICIPALITY, city.id)
        val locality     = service.createLocation("Leça",      LocationType.LOCALITY,     municipality.id)

        assertEquals(LocationType.LOCALITY, locality.type)
        assertEquals(municipality.id, locality.parentId)
    }

    // -------------------------------------------------------------------------
    // getLocationById
    // -------------------------------------------------------------------------

    @Test
    fun `getLocationById returns correct location`() {
        val country = service.createLocation("Portugal", LocationType.COUNTRY)

        val result = service.getLocationById(country.id)

        assertEquals(country, result)
    }

    @Test
    fun `getLocationById with non-existent id throws NotFoundException`() {
        assertFailsWith<NotFoundException> {
            service.getLocationById(99)
        }
    }

    // -------------------------------------------------------------------------
    // getChildrenLocations
    // -------------------------------------------------------------------------

    @Test
    fun `getChildrenLocations returns direct children only`() {
        val country = service.createLocation("Portugal", LocationType.COUNTRY)
        val norte   = service.createLocation("Norte",    LocationType.REGION, country.id)
        val sul     = service.createLocation("Sul",      LocationType.REGION, country.id)
        // grandchild — should NOT appear
        service.createLocation("Porto", LocationType.CITY, norte.id)

        val children = service.getChildrenLocations(country.id)

        assertEquals(2, children.size)
        assertTrue(children.any { it.name == "Norte" })
        assertTrue(children.any { it.name == "Sul" })
    }

    @Test
    fun `getChildrenLocations returns empty list when location has no children`() {
        val country = service.createLocation("Portugal", LocationType.COUNTRY)

        val children = service.getChildrenLocations(country.id)

        assertTrue(children.isEmpty())
    }

    @Test
    fun `getChildrenLocations with non-existent parentId throws NotFoundException`() {
        assertFailsWith<NotFoundException> {
            service.getChildrenLocations(99)
        }
    }

    // -------------------------------------------------------------------------
    // getHierarchyPath
    // -------------------------------------------------------------------------

    @Test
    fun `getHierarchyPath for COUNTRY returns only itself`() {
        val country = service.createLocation("Portugal", LocationType.COUNTRY)

        val path = service.getHierarchyPath(country.id)

        assertEquals(1, path.size)
        assertEquals(country, path[0])
    }

    @Test
    fun `getHierarchyPath returns path from root to target in order`() {
        val country = service.createLocation("Portugal", LocationType.COUNTRY)
        val region  = service.createLocation("Norte",    LocationType.REGION, country.id)
        val city    = service.createLocation("Porto",    LocationType.CITY,   region.id)

        val path = service.getHierarchyPath(city.id)

        assertEquals(3, path.size)
        assertEquals(country, path[0])
        assertEquals(region,  path[1])
        assertEquals(city,    path[2])
    }

    @Test
    fun `getHierarchyPath for full depth returns all 5 levels in order`() {
        val country      = service.createLocation("Portugal",   LocationType.COUNTRY)
        val region       = service.createLocation("Norte",      LocationType.REGION,       country.id)
        val city         = service.createLocation("Porto",      LocationType.CITY,         region.id)
        val municipality = service.createLocation("Matosinhos", LocationType.MUNICIPALITY, city.id)
        val locality     = service.createLocation("Leça",       LocationType.LOCALITY,     municipality.id)

        val path = service.getHierarchyPath(locality.id)

        assertEquals(listOf(country, region, city, municipality, locality), path)
    }

    @Test
    fun `getHierarchyPath with non-existent id throws NotFoundException`() {
        assertFailsWith<NotFoundException> {
            service.getHierarchyPath(99)
        }
    }


    // -------------------------------------------------------------------------
// listLocations
// -------------------------------------------------------------------------

    @Test
    fun `listLocations returns all locations when no type filter`() {
        val country = service.createLocation("Portugal", LocationType.COUNTRY)
        val region = service.createLocation("Norte", LocationType.REGION, country.id)

        val result = service.listLocations()

        assertEquals(2, result.size)
    }

    @Test
    fun `listLocations returns only locations of given type`() {
        val country = service.createLocation("Portugal", LocationType.COUNTRY)
        service.createLocation("Norte", LocationType.REGION, country.id)
        service.createLocation("Sul", LocationType.REGION, country.id)

        val result = service.listLocations(LocationType.REGION)

        assertEquals(2, result.size)
        assertTrue(result.all { it.type == LocationType.REGION })
    }

    @Test
    fun `listLocations returns empty list when no locations exist`() {
        val result = service.listLocations()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `listLocations returns empty list when no locations of given type exist`() {
        service.createLocation("Portugal", LocationType.COUNTRY)

        val result = service.listLocations(LocationType.CITY)

        assertTrue(result.isEmpty())
    }
}