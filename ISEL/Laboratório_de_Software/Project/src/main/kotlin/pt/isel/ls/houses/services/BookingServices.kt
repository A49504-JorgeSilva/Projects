package pt.isel.ls.houses.services

import pt.isel.ls.houses.data.BookingRepository
import pt.isel.ls.houses.data.HouseRepository
import pt.isel.ls.houses.domain.*
import java.time.LocalDate

class BookingServices(
    private val bookingRepository: BookingRepository,
    private val houseRepository: HouseRepository //for list of available houses
) {
    fun createBooking(houseId: Int, userId: Int, startDate: LocalDate, endDate: LocalDate): Booking {
        val dates = BookingDates(startDate, endDate)

        val overlaps = bookingRepository.getOverlappingBookings(houseId, dates.start, dates.end)
        check(overlaps.isEmpty()) { "The house is not available for the selected dates" }//check is for internal state, not user inputs, triggers when statement is false


        return bookingRepository.create(houseId, userId, dates.start, dates.end)
    }

    fun getBookingById(id: Int): Booking {
        return bookingRepository.getById(id)
            ?: throw NotFoundException("Booking with id $id not found")
    }

    fun listBookings(
        houseId: Int,
        startDate: LocalDate,
        endDate: LocalDate,
        skip: Int,
        limit: Int
    ): List<Booking> {
        require(startDate.isBefore(endDate)) { "Start date must be before end date" }
        val pagination = Pagination(skip, limit)

        return bookingRepository.getBookingsByHouseAndDates(houseId, startDate, endDate, pagination.skip, pagination.limit)
    }

    fun getAvailableHouses(start: LocalDate, end: LocalDate, skip: Int, limit: Int): List<House> {
        require(start.isBefore(end)) { "Start date must be before end date" }
        val pagination = Pagination(skip, limit)

        val bookedHouseIds = bookingRepository.getBookedHouseIds(start, end)

        return houseRepository.getAllHouses(skip = 0, limit = Int.MAX_VALUE)
            .filterNot { it.id in bookedHouseIds }
            .drop(pagination.skip)
            .take(pagination.limit)
    }

    fun listBookingsByUser(userId: Int, skip: Int, limit: Int): List<Booking>{
        val pagination = Pagination(skip, limit)

        return bookingRepository.getByUserId(userId, pagination.skip, pagination.limit)
    }

    fun deleteBooking(id: Int) {
        val deleted = bookingRepository.deleteBooking(id)
        if (!deleted) throw NotFoundException("Booking with id $id not found")
    }

    fun updateBooking(id: Int, startDate: LocalDate, endDate: LocalDate): Booking {
        val dates = BookingDates(startDate, endDate)

        val existing = bookingRepository.getById(id)
            ?: throw NotFoundException("Booking with id $id not found")

        if (existing.startDate == startDate && existing.endDate == endDate) {
            return existing //if no change do not touch DB
        }

        // Check overlaps excluding the booking being updated itself
        val overlaps = bookingRepository.getOverlappingBookings(existing.houseId, dates.start, dates.end)
            .filter { it.id != id }
        check(overlaps.isEmpty()) { "The house is not available for the selected dates" }

        return bookingRepository.updateBooking(id, dates.start, dates.end)
            ?: throw NotFoundException("Booking with id $id not found")
    }

    fun getAvailableDaysInMonth(houseId: Int, year: Int, month: Int): List<LocalDate>{
        require(month in 1..12) {"Month must be between 1 and 12"}

        val firstDay = LocalDate.of(year, month, 1)
        val lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth())
        // +1 because endDate is exclusive: a booking starting on the last day of the month would otherwise be missed
        val overlappingBookings = bookingRepository.getOverlappingBookings(houseId, firstDay, lastDay.plusDays(1))

        val bookedDates = mutableSetOf<LocalDate>()
        for (bookings in overlappingBookings) {
            var day = maxOf(bookings.startDate, firstDay)
            val end = minOf(bookings.endDate, lastDay.plusDays(1))
            while (day.isBefore(end)) {
                bookedDates.add(day)
                day = day.plusDays(1)
            }
        }

        val today = LocalDate.now()
        return (1..firstDay.lengthOfMonth())
            .map {firstDay.withDayOfMonth(it)}
            .filter{it !in bookedDates && !it.isBefore(today)}
    }
}
