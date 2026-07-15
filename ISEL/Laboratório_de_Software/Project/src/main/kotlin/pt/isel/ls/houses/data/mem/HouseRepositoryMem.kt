package pt.isel.ls.houses.data.mem

import pt.isel.ls.houses.data.HouseRepository
import pt.isel.ls.houses.domain.House
import pt.isel.ls.houses.domain.LocationType

class HouseRepositoryMem : HouseRepository {
    override fun getHouseById(id: Int): House? {
        return DbMem.houses[id]
    }

    override fun createHouse(title: String, locationId: Int, area: Double, price: Double, description: String, ownerId: Int): Int {
        val id = DbMem.nextHouseId++
        val house = House(id, title, locationId, area, price, description, ownerId)
        DbMem.houses[id] = house
        return id
    }

    override fun getAllHouses(searchTitle: String?, searchLocation: String?, skip: Int, limit: Int): List<House> {
        // Converte o mapa de houses para uma sequência
        var result = DbMem.houses.values.asSequence()

        // Filtro por house title (case-insensitive, contém)
        if (!searchTitle.isNullOrBlank()) {
            result = result.filter { it.title.contains(searchTitle, ignoreCase = true) }
        }

        // Filtro por location name (case-insensitive, contém)
        if (!searchLocation.isNullOrBlank()) {
            val matchingLocationIds = DbMem.locations.values
                .filter { it.name.contains(searchLocation, ignoreCase = true) }
                .map { it.id }
                .toSet()
            result = result.filter { it.locationId in matchingLocationIds }
        }

        return result.drop(skip).take(limit).toList()
    }
}