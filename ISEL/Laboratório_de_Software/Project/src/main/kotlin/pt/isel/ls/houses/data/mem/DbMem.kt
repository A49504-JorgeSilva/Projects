package pt.isel.ls.houses.data.mem

import pt.isel.ls.houses.domain.Booking
import pt.isel.ls.houses.domain.House
import pt.isel.ls.houses.domain.Location
import pt.isel.ls.houses.domain.User

object DbMem {
    val users : MutableMap<Int, User> = mutableMapOf()
    val locations: MutableMap<Int, Location> = mutableMapOf()
    val bookings: MutableMap<Int, Booking> = mutableMapOf()
    val houses: MutableMap<Int, House> = mutableMapOf()
    val userPasswordHashes: MutableMap<Int, String> = mutableMapOf()

    var nextUserId: Int = 1
    var nextLocationId: Int = 1
    var nextBookingId: Int = 1
    var nextHouseId: Int = 1

    fun clearAll() {
        users.clear()
        locations.clear()
        bookings.clear()
        houses.clear()
        userPasswordHashes.clear()

        nextUserId = 1
        nextLocationId = 1
        nextBookingId = 1
        nextHouseId = 1
    }
}