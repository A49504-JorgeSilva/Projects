package pt.isel.ls.houses.domain

enum class LocationType {
    COUNTRY,
    REGION,
    CITY,
    MUNICIPALITY,
    LOCALITY
}

data class Location(
    val id: Int,
    val name: String,
    val type: LocationType,
    val parentId: Int?
)