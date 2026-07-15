package pt.isel.ls.houses.data

import pt.isel.ls.houses.domain.House
import pt.isel.ls.houses.domain.LocationType

interface HouseRepository {
    fun createHouse(title: String, locationId: Int, area: Double, price: Double, description: String, ownerId: Int): Int
    fun getHouseById(id: Int): House?
    fun getAllHouses(searchTitle: String? = null, searchLocation: String? = null, skip: Int, limit: Int): List<House>
}