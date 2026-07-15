package pt.isel.ls.houses.services

import pt.isel.ls.houses.data.mem.BookingRepositoryMem
import pt.isel.ls.houses.data.mem.DbMem
import pt.isel.ls.houses.data.mem.HouseRepositoryMem
import pt.isel.ls.houses.data.mem.LocationRepositoryMem
import pt.isel.ls.houses.domain.LocationType
import pt.isel.ls.houses.domain.NotFoundException
import java.time.LocalDate
import kotlin.test.*

class BookingServicesTest {

    private val houseRepository = HouseRepositoryMem()
    private val locationRepo = LocationRepositoryMem()
    private val houseServices = HouseServices(houseRepository, locationRepo)

    private val bookingRepository = BookingRepositoryMem()
    private val bookingServices = BookingServices(bookingRepository, houseRepository)

    @BeforeTest
    fun setup() {
        DbMem.clearAll()//check
        repeat(4) { locationRepo.create("Locality ${it + 1}", LocationType.LOCALITY, null) }
    }

    // createBooking
    @Test
    fun `createBooking should create a new booking and return its id`() {
        //arrange
        val houseId = 1
        val userId = 1
        val startDate = LocalDate.now().plusDays(1)
        val endDate = LocalDate.now().plusDays(5)

        //act
        val bookingId = bookingServices.createBooking(houseId, userId, startDate, endDate).id
        val createdBooking = bookingServices.getBookingById(bookingId)

        //assert
        assertEquals(bookingId, createdBooking.id)
        assertEquals(houseId, createdBooking.houseId)
        assertEquals(userId, createdBooking.userId)
        assertEquals(startDate, createdBooking.startDate)
        assertEquals(endDate, createdBooking.endDate)
    }

    @Test
    fun `createBooking should throw IllegalArgumentException if start date is after end date`() {
        //arrange
        val startDate = LocalDate.now().plusDays(5)
        val endDate = LocalDate.now().plusDays(1)

        //act & assert
        assertFailsWith<IllegalArgumentException> {
            bookingServices.createBooking(1, 1, startDate, endDate)
        }
    }

    @Test
    fun `createBooking should throw IllegalArgumentException if start date is in the past`() {
        //arrange
        val startDate = LocalDate.now().minusDays(1)
        val endDate = LocalDate.now().plusDays(5)

        //act & assert
        assertFailsWith<IllegalArgumentException> {
            bookingServices.createBooking(1, 1, startDate, endDate)
        }
    }

    @Test
    fun `createBooking should throw IllegalStateException if house is already booked for those dates`() {
        //arrange
        val houseId = 1
        val userId = 1
        val startDate = LocalDate.now().plusDays(1)
        val endDate = LocalDate.now().plusDays(10)

        //book it once successfully
        bookingServices.createBooking(houseId, userId, startDate, endDate)

        //act & assert - overlapping dates
        val overlappingStart = LocalDate.now().plusDays(5)
        val overlappingEnd = LocalDate.now().plusDays(15)

        assertFailsWith<IllegalStateException> {
            bookingServices.createBooking(houseId, 2, overlappingStart, overlappingEnd)
        }
    }

    @Test
    fun `createBooking allows same dates if the houseId is different`() {
        //arrange
        val startDate = LocalDate.now().plusDays(1)
        val endDate = LocalDate.now().plusDays(5)

        //act
        val booking1Id = bookingServices.createBooking(1, 1, startDate, endDate).id
        val booking2Id = bookingServices.createBooking(2, 1, startDate, endDate).id

        //assert
        assertNotEquals(booking1Id, booking2Id)
    }

    // getBookingById

    @Test
    fun `getBookingById should throw NotFoundException for non-existent booking id`() {
        //arrange
        val nonExistentId = 999

        //act & assert
        assertFailsWith<NotFoundException> {
            bookingServices.getBookingById(nonExistentId)
        }
    }

    // listBookings

    @Test
    fun `listBookings should return a list of bookings with pagination`() {
        //arrange
        val houseId = 1
        val start = LocalDate.now().plusDays(1)
        val end = LocalDate.now().plusDays(30)

        bookingServices.createBooking(houseId, 1, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4))
        bookingServices.createBooking(houseId, 2, LocalDate.now().plusDays(6), LocalDate.now().plusDays(8))
        bookingServices.createBooking(houseId, 3, LocalDate.now().plusDays(10), LocalDate.now().plusDays(12))

        //act
        val firstPage = bookingServices.listBookings(houseId, start, end, skip = 0, limit = 2)
        val secondPage = bookingServices.listBookings(houseId, start, end, skip = 2, limit = 2)

        //assert
        assertEquals(2, firstPage.size)
        assertEquals(1, secondPage.size)
    }

    @Test
    fun `listBookings should include bookings that partially overlap the interval`() {
        val houseId = 1
        val start = LocalDate.now().plusDays(5)
        val end = LocalDate.now().plusDays(10)

        // começa antes e acaba dentro do intervalo
        bookingServices.createBooking(houseId, 1, LocalDate.now().plusDays(3), LocalDate.now().plusDays(7))
        // começa no dia a seguir ao anterior, totalmente contida
        bookingServices.createBooking(houseId, 2, LocalDate.now().plusDays(7), LocalDate.now().plusDays(9))
        // começa no dia a seguir ao anterior, acaba depois do intervalo
        bookingServices.createBooking(houseId, 3, LocalDate.now().plusDays(9), LocalDate.now().plusDays(12))
        // totalmente fora — não deve aparecer
        bookingServices.createBooking(houseId, 4, LocalDate.now().plusDays(15), LocalDate.now().plusDays(20))

        val result = bookingServices.listBookings(houseId, start, end, skip = 0, limit = 10)

        assertEquals(3, result.size)
    }

    @Test
    fun `listBookings should throw IllegalArgumentException for invalid pagination parameters`() {
        //arrange
        val houseId = 1
        val start = LocalDate.now()
        val end = LocalDate.now().plusDays(5)

        //act & assert
        assertFailsWith<IllegalArgumentException> {
            bookingServices.listBookings(houseId, start, end, skip = -1, limit = 10)
        }
        assertFailsWith<IllegalArgumentException> {
            bookingServices.listBookings(houseId, start, end, skip = 0, limit = -5)
        }
    }

    // getAvailableHouses

    @Test
    fun `getAvailableHouses should return available houses when no bookings exist`() {
        //arrange
        houseServices.createNewHouse("Beach House", 1, 120.5, 150.0, "Great view", 1)
        houseServices.createNewHouse("Mountain Cabin", 2, 80.0, 95.0, "Cozy fireplace", 2)

        val start = LocalDate.now().plusDays(1)
        val end = LocalDate.now().plusDays(5)

        //act
        val availableHouses = bookingServices.getAvailableHouses(start, end, skip = 0, limit = 10)

        //assert
        assertTrue(availableHouses.isNotEmpty(), "Should return available houses")
        assertTrue(availableHouses.size <= 10, "Should respect the limit of 10")
    }

    @Test
    fun `getAvailableHouses should correctly apply skip and limit for pagination`() {
        //arrange
        houseServices.createNewHouse("House 1", 1, 50.0, 100.0, "Test 1", 1)
        houseServices.createNewHouse("House 2", 1, 50.0, 100.0, "Test 2", 2)
        houseServices.createNewHouse("House 3", 1, 50.0, 100.0, "Test 3", 3)
        houseServices.createNewHouse("House 4", 1, 50.0, 100.0, "Test 4", 4)

        val start = LocalDate.now().plusDays(10)
        val end = LocalDate.now().plusDays(15)

        //act
        val firstPage = bookingServices.getAvailableHouses(start, end, skip = 0, limit = 2)
        val secondPage = bookingServices.getAvailableHouses(start, end, skip = 2, limit = 2)

        //assert
        assertEquals(2, firstPage.size)
        assertEquals(2, secondPage.size)
        assertTrue(firstPage.intersect(secondPage.toSet()).isEmpty(), "Pages should not overlap")
    }

    @Test
    fun `getAvailableHouses should throw IllegalArgumentException if start date is after end date`() {
        //arrange
        val start = LocalDate.now().plusDays(5)
        val end = LocalDate.now().plusDays(1)

        //act & assert
        assertFailsWith<IllegalArgumentException> {
            bookingServices.getAvailableHouses(start, end, skip = 0, limit = 10)
        }
    }

    @Test
    fun `getAvailableHouses should throw IllegalArgumentException for invalid pagination parameters`() {
        //arrange
        val start = LocalDate.now().plusDays(1)
        val end = LocalDate.now().plusDays(5)

        //act & assert
        assertFailsWith<IllegalArgumentException> {
            bookingServices.getAvailableHouses(start, end, skip = -1, limit = 10)
        }
        assertFailsWith<IllegalArgumentException> {
            bookingServices.getAvailableHouses(start, end, skip = 0, limit = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            bookingServices.getAvailableHouses(start, end, skip = 0, limit = -5)
        }
    }

    @Test
    fun `getAvailableHouses should exclude houses that are booked during the overlapping period`() {
        //arrange
        val house1Id = houseServices.createNewHouse("Available House", 1, 50.0, 100.0, "Free", 1)
        val house2Id = houseServices.createNewHouse("Booked House", 1, 50.0, 100.0, "Taken", 2)

        val start = LocalDate.now().plusDays(1)
        val end = LocalDate.now().plusDays(5)

        bookingServices.createBooking(house2Id, 1, start, end)

        //act
        val availableHouses = bookingServices.getAvailableHouses(start, end, skip = 0, limit = 10)

        //assert
        assertEquals(1, availableHouses.size)
        assertEquals(house1Id, availableHouses.first().id)
    }


    // listBookingsByUser


    @Test
    fun `listBookingsByUser should return only bookings belonging to that user`() {
        //arrange
        bookingServices.createBooking(1, userId = 10, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3))
        bookingServices.createBooking(2, userId = 10, LocalDate.now().plusDays(5), LocalDate.now().plusDays(7))
        bookingServices.createBooking(3, userId = 99, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3))

        //act
        val result = bookingServices.listBookingsByUser(userId = 10, skip = 0, limit = 10)

        //assert
        assertEquals(2, result.size)
        assertTrue(result.all { it.userId == 10 })
    }

    @Test
    fun `listBookingsByUser should return empty list when user has no bookings`() {
        //act
        val result = bookingServices.listBookingsByUser(userId = 42, skip = 0, limit = 10)

        //assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `listBookingsByUser should correctly apply skip and limit for pagination`() {
        //arrange
        val userId = 7
        bookingServices.createBooking(1, userId, LocalDate.now().plusDays(1), LocalDate.now().plusDays(2))
        bookingServices.createBooking(2, userId, LocalDate.now().plusDays(3), LocalDate.now().plusDays(4))
        bookingServices.createBooking(3, userId, LocalDate.now().plusDays(5), LocalDate.now().plusDays(6))

        //act
        val firstPage = bookingServices.listBookingsByUser(userId, skip = 0, limit = 2)
        val secondPage = bookingServices.listBookingsByUser(userId, skip = 2, limit = 2)

        //assert
        assertEquals(2, firstPage.size)
        assertEquals(1, secondPage.size)
        assertTrue(firstPage.intersect(secondPage.toSet()).isEmpty(), "Pages should not overlap")
    }

    @Test
    fun `listBookingsByUser should throw IllegalArgumentException for invalid pagination parameters`() {
        assertFailsWith<IllegalArgumentException> {
            bookingServices.listBookingsByUser(1, skip = -1, limit = 10)
        }
        assertFailsWith<IllegalArgumentException> {
            bookingServices.listBookingsByUser(1, skip = 0, limit = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            bookingServices.listBookingsByUser(1, skip = 0, limit = -5)
        }
    }


    // deleteBooking


    @Test
    fun `deleteBooking should remove an existing booking`() {
        //arrange
        val bookingId = bookingServices.createBooking(1, 1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)).id

        //act
        bookingServices.deleteBooking(bookingId)

        //assert - booking should no longer be found
        assertFailsWith<NotFoundException> {
            bookingServices.getBookingById(bookingId)
        }
    }

    @Test
    fun `deleteBooking should throw NotFoundException for a non-existent booking`() {
        assertFailsWith<NotFoundException> {
            bookingServices.deleteBooking(999)
        }
    }

    @Test
    fun `deleteBooking should only remove the targeted booking and leave others intact`() {
        //arrange
        val booking1Id = bookingServices.createBooking(1, 1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3)).id
        val booking2Id = bookingServices.createBooking(2, 1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3)).id

        //act
        bookingServices.deleteBooking(booking1Id)

        //assert
        assertFailsWith<NotFoundException> { bookingServices.getBookingById(booking1Id) }
        val remaining = bookingServices.getBookingById(booking2Id)
        assertEquals(booking2Id, remaining.id)
    }


    // updateBooking


    @Test
    fun `updateBooking should replace the date interval of an existing booking`() {
        //arrange
        val bookingId = bookingServices.createBooking(1, 1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)).id
        val newStart = LocalDate.now().plusDays(10)
        val newEnd = LocalDate.now().plusDays(15)

        //act
        val updated = bookingServices.updateBooking(bookingId, newStart, newEnd)

        //assert
        assertEquals(bookingId, updated.id)
        assertEquals(newStart, updated.startDate)
        assertEquals(newEnd, updated.endDate)
    }

    @Test
    fun `updateBooking should throw NotFoundException for a non-existent booking`() {
        assertFailsWith<NotFoundException> {
            bookingServices.updateBooking(999, LocalDate.now().plusDays(1), LocalDate.now().plusDays(5))
        }
    }

    @Test
    fun `updateBooking should throw IllegalArgumentException if new start date is after new end date`() {
        //arrange
        val bookingId = bookingServices.createBooking(1, 1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)).id

        //act & assert
        assertFailsWith<IllegalArgumentException> {
            bookingServices.updateBooking(bookingId, LocalDate.now().plusDays(10), LocalDate.now().plusDays(2))
        }
    }

    @Test
    fun `updateBooking should throw IllegalArgumentException if new start date is in the past`() {
        //arrange
        val bookingId = bookingServices.createBooking(1, 1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(5)).id

        //act & assert
        assertFailsWith<IllegalArgumentException> {
            bookingServices.updateBooking(bookingId, LocalDate.now().minusDays(1), LocalDate.now().plusDays(5))
        }
    }

    @Test
    fun `updateBooking should throw IllegalStateException if new dates overlap with another booking on the same house`() {
        //arrange
        val houseId = 1
        bookingServices.createBooking(houseId, 1, LocalDate.now().plusDays(10), LocalDate.now().plusDays(15))
        val bookingToUpdateId = bookingServices.createBooking(houseId, 2, LocalDate.now().plusDays(20), LocalDate.now().plusDays(25)).id

        //act & assert - new dates overlap with the first booking
        assertFailsWith<IllegalStateException> {
            bookingServices.updateBooking(bookingToUpdateId, LocalDate.now().plusDays(12), LocalDate.now().plusDays(18))
        }
    }

    @Test
    fun `updateBooking should not consider the booking being updated as an overlap with itself`() {
        //arrange - a booking that occupies days 1-10
        val bookingId = bookingServices.createBooking(1, 1, LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)).id

        //act - updating to a slightly shifted window within the same range should not self-conflict
        val updated = bookingServices.updateBooking(bookingId, LocalDate.now().plusDays(2), LocalDate.now().plusDays(8))

        //assert
        assertEquals(LocalDate.now().plusDays(2), updated.startDate)
        assertEquals(LocalDate.now().plusDays(8), updated.endDate)
    }


    // getAvailableDaysInMonth



    @Test
    fun `getAvailableDaysInMonth should return all days when no bookings exist`() {
        val year = 2030
        val month = 8 // August - safely in the future

        val available = bookingServices.getAvailableDaysInMonth(1, year, month)

        assertEquals(31, available.size) // August has 31 days
        assertEquals(LocalDate.of(year, month, 1), available.first())
        assertEquals(LocalDate.of(year, month, 31), available.last())
    }

    @Test
    fun `getAvailableDaysInMonth should exclude booked days`() {
        val year = 2030
        val month = 8

        bookingServices.createBooking(1, 1, LocalDate.of(year, month, 10), LocalDate.of(year, month, 15))

        val available = bookingServices.getAvailableDaysInMonth(1, year, month)

        assertFalse(available.contains(LocalDate.of(year, month, 10)))
        assertFalse(available.contains(LocalDate.of(year, month, 14)))
        assertTrue(available.contains(LocalDate.of(year, month, 9)))
        assertTrue(available.contains(LocalDate.of(year, month, 15)))
    }

    @Test
    fun `getAvailableDaysInMonth should exclude past days`() {
        val today = LocalDate.now()
        val available = bookingServices.getAvailableDaysInMonth(1, today.year, today.monthValue)

        assertTrue(available.none { it.isBefore(today) })
    }

    @Test
    fun `getAvailableDaysInMonth should return empty list when all days are booked`() {
        val year = 2030
        val month = 6 // June has 30 days

        // book the entire month (end is exclusive in overlap logic, so use July 1)
        bookingServices.createBooking(1, 1, LocalDate.of(year, month, 1), LocalDate.of(year, month + 1, 1))

        val available = bookingServices.getAvailableDaysInMonth(1, year, month)

        assertTrue(available.isEmpty())
    }

    @Test
    fun `getAvailableDaysInMonth should handle booking that starts before the month`() {
        val year = 2030
        val month = 8

        bookingServices.createBooking(1, 1, LocalDate.of(year, month - 1, 25), LocalDate.of(year, month, 5))

        val available = bookingServices.getAvailableDaysInMonth(1, year, month)

        assertFalse(available.contains(LocalDate.of(year, month, 1)))
        assertFalse(available.contains(LocalDate.of(year, month, 4)))
        assertTrue(available.contains(LocalDate.of(year, month, 5)))
    }

    @Test
    fun `getAvailableDaysInMonth should handle booking that ends after the month`() {
        val year = 2030
        val month = 8

        bookingServices.createBooking(1, 1, LocalDate.of(year, month, 28), LocalDate.of(year, month + 1, 5))

        val available = bookingServices.getAvailableDaysInMonth(1, year, month)

        assertFalse(available.contains(LocalDate.of(year, month, 28)))
        assertFalse(available.contains(LocalDate.of(year, month, 31)))
        assertTrue(available.contains(LocalDate.of(year, month, 27)))
    }

    @Test
    fun `getAvailableDaysInMonth should return empty list for a month fully in the past`() {
        val available = bookingServices.getAvailableDaysInMonth(1, 2020, 1)

        assertTrue(available.isEmpty())
    }

    @Test
    fun `getAvailableDaysInMonth should throw IllegalArgumentException for invalid month`() {
        assertFailsWith<IllegalArgumentException> {
            bookingServices.getAvailableDaysInMonth(1, 2030, 0)
        }
        assertFailsWith<IllegalArgumentException> {
            bookingServices.getAvailableDaysInMonth(1, 2030, 13)
        }
    }
}
