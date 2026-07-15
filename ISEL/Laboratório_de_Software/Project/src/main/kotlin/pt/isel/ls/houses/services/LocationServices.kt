package pt.isel.ls.houses.services

import pt.isel.ls.houses.data.LocationRepository
import pt.isel.ls.houses.domain.*

class LocationServices(
    private val locationRepository: LocationRepository
){

    /**
     * The hierarchy enforces that a child location must have a parent
     * of a strictly higher type.
     * COUNTRY > REGION > CITY > MUNICIPALITY > LOCALITY
     */
    private val typeHierarchy = listOf(
        LocationType.COUNTRY,
        LocationType.REGION,
        LocationType.CITY,
        LocationType.MUNICIPALITY,
        LocationType.LOCALITY
    )

    fun createLocation(name: String, type: LocationType, parentId: Int? = null): Location {
        if (name.isBlank()) throw InvalidInputException("Location name cannot be blank")

        val trimmedName = name.trim()

        if (parentId != null) {
            val parentLocation = locationRepository.getById(parentId)
                ?: throw NotFoundException("Parent location with id $parentId does not exist")

            val parentRank = typeHierarchy.indexOf(parentLocation.type)
            val childRank = typeHierarchy.indexOf(type)

            if (childRank <= parentRank)
                throw InvalidHierarchyException("Invalid location hierarchy: ${type.name} cannot be a child of ${parentLocation.type.name}")

        } else {
            if (type != LocationType.COUNTRY)
                throw InvalidInputException("Only a COUNTRY can be created without a parent location")

        }

        val existingWithSameName = locationRepository.getByName(trimmedName)
        if (existingWithSameName != null && existingWithSameName.parentId == parentId) {
            throw ConflictException(
                "A location named '$trimmedName' already exists under the same parent"
            )
        }

        return locationRepository.create(trimmedName, type, parentId)
    }

    fun getLocationById(id: Int): Location {
        return locationRepository.getById(id)
            ?: throw NotFoundException("Location with id $id not found")
    }

    fun getChildrenLocations(parentId: Int): List<Location> {
        locationRepository.getById(parentId)
            ?: throw NotFoundException("Parent location with id $parentId not found")

        return locationRepository.getByParentId(parentId)
    }

    fun getHierarchyPath(id: Int): List<Location> {
        val target = locationRepository.getById(id)
            ?: throw NotFoundException("Location with id $id not found")
        val path = mutableListOf<Location>()
        var current: Location? = target

        while (current != null) {
            path.add(current)
            current = current.parentId?.let { locationRepository.getById(it) }
        }

        return path.reversed()
    }

    fun listLocations(type: LocationType? = null): List<Location> {
        return locationRepository.getAll(type)
    }

    fun listLocationsByPartialName(name: String): List<Location> {
        return locationRepository.getByPartialName(name)
    }
}