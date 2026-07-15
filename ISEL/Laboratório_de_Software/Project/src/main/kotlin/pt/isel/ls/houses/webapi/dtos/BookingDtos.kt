package pt.isel.ls.houses.webapi.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CreateBookingRequest(
    val hid: Int,
    val startDate: String,//here as String for easier conversion to and from JSON
    val endDate: String
)

@Serializable
data class CreateBookingResponse(
    val id: Int
)

@Serializable
data class BookingResponse(
    val id: Int,
    val startDate: String,
    val endDate: String,
    val userId: Int,
    val houseId: Int
)

@Serializable
data class UpdateBookingRequest(
    val startDate: String,
    val endDate: String
)

@Serializable
data class AvailableDaysResponse(
    val availableDays: List<String>
)