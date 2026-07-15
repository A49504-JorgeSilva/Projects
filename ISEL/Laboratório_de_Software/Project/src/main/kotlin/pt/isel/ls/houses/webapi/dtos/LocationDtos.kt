package pt.isel.ls.houses.webapi.dtos

import kotlinx.serialization.Serializable
import pt.isel.ls.houses.domain.LocationType

@Serializable
data class CreateLocationRequest(
    val name: String,
    val type: LocationType,
    val parentId: Int? = null
)

@Serializable
data class CreateLocationResponse(
    val locationId: Int,
)

@Serializable
data class LocationResponse(
    val id: Int,
    val name: String,
    val type: String,
    val parentId: Int?
)
