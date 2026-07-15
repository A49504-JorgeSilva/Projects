package pt.isel.ls.houses.data.mem

import pt.isel.ls.houses.data.BookingRepository
import pt.isel.ls.houses.domain.Booking
import java.time.LocalDate


class BookingRepositoryMem: BookingRepository {
    override fun create(houseId: Int, userId: Int, startDate: LocalDate, endDate: LocalDate): Booking {//constructor with characteristics in Services
        val id = DbMem.nextBookingId++
        val booking = Booking(id, startDate, endDate, userId, houseId)
        DbMem.bookings[id] = booking
        return booking
    }

    override fun getById(id: Int): Booking? {//returns Booking type OR null, therefore with the "?"
        return DbMem.bookings[id]
    }

    override fun getOverlappingBookings(houseId: Int, start: LocalDate, end: LocalDate): List<Booking> { //check for overlapping dates when creating a booking
        return DbMem.bookings.values.filter {//.values, as bookings is a map of <Int, Booking>, .filter returns a List<Booking>
            it.houseId == houseId && it.startDate < end && it.endDate > start
        }
    }

    override fun getBookingsByHouseAndDates(
        houseId: Int,
        start: LocalDate,
        end: LocalDate,
        skip: Int,//skip and limit for paging
        limit: Int
    ): List<Booking> {
        return DbMem.bookings.values
            .filter { it.houseId == houseId && it.startDate < end && it.endDate > start }
            .sortedBy { it.startDate }
            .drop(skip)
            .take(limit)
    }

    override fun getBookedHouseIds(start: LocalDate, end: LocalDate): Set<Int> {//for available (here booked) houses during given period
        return DbMem.bookings.values.filter { it.startDate < end && it.endDate > start }
            .map { it.houseId }.toSet()//get just house ids and to Set to remove repetitions
    }

    override fun getByUserId(userId: Int, skip: Int, limit: Int): List<Booking> {
        return DbMem.bookings.values
            .filter { it.userId == userId }
            .sortedBy { it.startDate }
            .drop(skip)
            .take(limit)
    }

    override fun deleteBooking(id: Int): Boolean {
        return DbMem.bookings.remove(id) != null
    }

    override fun updateBooking(id: Int, startDate: LocalDate, endDate: LocalDate): Booking? {
        val existing = DbMem.bookings[id] ?: return null
        val updated = existing.copy(startDate = startDate, endDate = endDate)
        DbMem.bookings[id] = updated
        return updated
    }

}
