package pt.isel.ls.houses.domain

import java.time.LocalDate

data class Booking(
    val id: Int,
    val startDate: LocalDate,
    val endDate: LocalDate, // exclusive: the checkout day itself is not part of the stay
    val userId: Int,
    val houseId: Int
)

data class BookingDates(val start: LocalDate, val end: LocalDate) {
    init {
        require(start.isBefore(end)) { "Start date must be before the end date" }
        require(!start.isBefore(LocalDate.now())) { "Cannot book dates from the past" }
    }
}

data class Pagination(val skip: Int, val limit: Int) {
    init {
        require(skip >= 0) { "Skip must be a non-negative integer" }
        require(limit > 0) { "Limit must be a positive integer" }
    }
}