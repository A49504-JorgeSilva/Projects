package pt.isel.ls.houses.data

import pt.isel.ls.houses.domain.Booking
import java.time.LocalDate

interface BookingRepository {
    fun create(houseId: Int, userId: Int, startDate: LocalDate, endDate: LocalDate): Booking
    fun getById(id: Int): Booking?
    fun getOverlappingBookings(houseId: Int, start: LocalDate, end: LocalDate): List<Booking>
    fun getBookingsByHouseAndDates(houseId: Int, start: LocalDate, end: LocalDate, skip: Int, limit: Int): List<Booking>
    fun getBookedHouseIds(start: LocalDate, end: LocalDate): Set<Int>
    fun getByUserId(userId: Int, skip: Int, limit: Int): List<Booking>
    fun deleteBooking(id: Int): Boolean
    fun updateBooking(id: Int, startDate: LocalDate, endDate: LocalDate): Booking?
}
