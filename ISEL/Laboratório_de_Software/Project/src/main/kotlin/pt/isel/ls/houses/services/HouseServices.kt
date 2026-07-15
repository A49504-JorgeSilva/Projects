package pt.isel.ls.houses.services

import pt.isel.ls.houses.data.HouseRepository
import pt.isel.ls.houses.data.LocationRepository
import pt.isel.ls.houses.domain.House
import pt.isel.ls.houses.domain.InvalidInputException
import pt.isel.ls.houses.domain.LocationType
import pt.isel.ls.houses.domain.NotFoundException
import pt.isel.ls.houses.domain.Pagination

class HouseServices (
    private val houseRepository: HouseRepository,
    private val locationRepository: LocationRepository
) {
    fun getHouse(id: Int): House {
        return houseRepository.getHouseById(id) ?: throw NotFoundException("House with id $id not found")
    }

    fun createNewHouse(title: String, locationId: Int, area: Double, price: Double, description: String, ownerId: Int): Int {
        val location = locationRepository.getById(locationId)
            ?: throw NotFoundException("Location with id $locationId not found")

        if (location.type != LocationType.LOCALITY)
            throw InvalidInputException("Houses can only be created in a LOCALITY, but location ${location.name} is a ${location.type}")

        return houseRepository.createHouse(title, locationId, area, price, description, ownerId)
    }

    fun listHouses(searchTitle: String? = null, searchLocation: String? = null, skip: Int, limit: Int): List<House> {
        val pagination = Pagination(skip, limit)
        return houseRepository.getAllHouses(searchTitle, searchLocation, pagination.skip, pagination.limit)
    }
}