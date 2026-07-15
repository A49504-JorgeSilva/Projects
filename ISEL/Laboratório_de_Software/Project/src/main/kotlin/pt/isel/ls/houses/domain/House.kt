package pt.isel.ls.houses.domain

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
data class House(
    val id: Int,
    val title: String,
    val locationId: Int,
    val areaSqMt: Double,
    val pricePerNight: Double,
    val description: String,
    val ownerId: Int
){
    init {
        require(title.isNotBlank()) { "Title cannot be blank" }
        require(locationId > 0) { "Location ID must be a positive integer" }
        require(areaSqMt > 0) { "Area must be a positive number" }
        require(pricePerNight > 0) { "Price must be a positive number" }
        require(description.isNotBlank()) { "Description cannot be blank" }
        require(ownerId > 0) { "Owner ID must be a positive integer" }
    }
}