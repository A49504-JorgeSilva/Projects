package pt.isel.ls.houses.data.mem

import pt.isel.ls.houses.data.LocationRepository
import pt.isel.ls.houses.domain.Location
import pt.isel.ls.houses.domain.LocationType

class LocationRepositoryMem : LocationRepository {
    override fun create(name: String, type: LocationType, parentId: Int?): Location {
        val id = DbMem.nextLocationId++
        val location = Location(id, name, type, parentId)
        DbMem.locations[id] = location
        return location
    }

    override fun getById(id: Int): Location? {
        return DbMem.locations[id]
    }

    override fun getByName(name: String): Location? {
        return DbMem.locations.values.find {
            it.name.equals(name, ignoreCase = true)
        }
    }

    override fun getByPartialName(name: String): List<Location> {
        return DbMem.locations.values.filter {
            it.name.contains(name, ignoreCase = true)
        }
    }


    override fun getByParentId(parentId: Int): List<Location> {
        return DbMem.locations.values.filter{
            it.parentId == parentId
        }
    }

    override fun getAll(type: LocationType?): List<Location> {
        return if (type == null)
            DbMem.locations.values.toList()
        else
            DbMem.locations.values.filter { it.type == type }
    }
}