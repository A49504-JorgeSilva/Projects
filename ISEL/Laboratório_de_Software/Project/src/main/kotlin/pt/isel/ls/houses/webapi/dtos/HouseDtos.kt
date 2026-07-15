package pt.isel.ls.houses.webapi.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CreateHouseRequest(
    val title: String,
    val locationId: Int,
    val areaSqMt: Double,
    val pricePerNight: Double,
    val description: String
)

@Serializable
data class CreateHouseResponse(
    val id: Int
)

@Serializable
data class GetHouseResponse(
    val id: Int,
    val title: String,
    val locationId: Int,
    val areaSqMt: Double,
    val pricePerNight: Double,
    val description: String,
    val ownerId: Int
)

@Serializable
data class GetHousesResponse(
    val houses: List<GetHouseResponse>
)