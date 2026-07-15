package pt.isel.ls.houses.server

import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.singlePageApp
import org.slf4j.LoggerFactory
import pt.isel.ls.houses.services.UserServices
import pt.isel.ls.houses.webapi.UserHandler
import org.http4k.server.Jetty
import org.http4k.server.asServer
import pt.isel.ls.houses.data.getDataSource
import pt.isel.ls.houses.webapi.BookingHandler
import pt.isel.ls.houses.services.BookingServices
//import pt.isel.ls.houses.data.mem.BookingRepositoryMem
//import pt.isel.ls.houses.data.mem.LocationRepositoryMem
//import pt.isel.ls.houses.data.mem.HouseRepositoryMem
//import pt.isel.ls.houses.data.mem.UserRepositoryMem
import pt.isel.ls.houses.data.postgres.HouseRepositoryPostgres
import pt.isel.ls.houses.data.postgres.BookingRepositoryPostgres
import pt.isel.ls.houses.data.postgres.LocationRepositoryPostgres
import pt.isel.ls.houses.data.postgres.UserRepositoryPostgres
import pt.isel.ls.houses.services.LocationServices
import pt.isel.ls.houses.webapi.LocationHandler
import pt.isel.ls.houses.services.HouseServices
import pt.isel.ls.houses.webapi.HouseHandler

private val logger = LoggerFactory.getLogger("Server")

fun main() {
    val dataSource = getDataSource()

    val houseRepo = HouseRepositoryPostgres(dataSource)//declare every repo first because it may be used by multiple services
    val userRepo = UserRepositoryPostgres(dataSource)
    val bookingRepo = BookingRepositoryPostgres(dataSource)
    val locationRepo = LocationRepositoryPostgres(dataSource)

    val houseService = HouseServices(houseRepo, locationRepo)
    val userService = UserServices(userRepo)
    val bookingService = BookingServices(bookingRepo, houseRepo)
    val locationService = LocationServices(locationRepo)

    val houseHandler = HouseHandler(houseService, userService)
    val userHandler = UserHandler(userService, bookingService)
    val bookingHandler = BookingHandler(bookingService, userService)
    val locationHandler = LocationHandler(locationService, userService)

    val app = routes(
        userHandler.routes(),
        bookingHandler.routes(),
        locationHandler.routes(),
        houseHandler.routes(),
        singlePageApp(ResourceLoader.Directory("static-content")),
    )

    val server = app.asServer(Jetty(8080)).start()
    logger.info("server started listening")
    server.block();
}