package pt.isel.ls.houses.data

import pt.isel.ls.houses.domain.Location
import pt.isel.ls.houses.domain.LocationType

interface LocationRepository {
    fun create(name: String, type: LocationType, parentId: Int?): Location
    fun getById(id: Int): Location?
    fun getByName(name: String): Location?
    fun getByPartialName(name: String): List<Location>
    fun getByParentId(parentId: Int): List<Location>
    fun getAll(type: LocationType? = null): List<Location>
}