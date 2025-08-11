package pt.isel.carTests

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.util.PSQLException
import pt.isel.Repository
import pt.isel.RepositoryReflect
import pt.isel.car.Car
import pt.isel.car.Engine
import pt.isel.car.Fuel
import pt.isel.car.Transmission
import pt.isel.car.TransmissionType
import pt.isel.car.dao.CarDBInserterJdbc
import pt.isel.loadDynamicRepo
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.assertEquals
import kotlin.test.assertNull

val CAR_DB_URL = System.getenv("CAR_DB_URL") ?: throw Exception("Missing env var CAR_DB_URL")

class CarRepositoryTest {
    companion object {
        private val connection: Connection = DriverManager.getConnection(CAR_DB_URL)

        @JvmStatic
        fun repositories() =
            listOf<Repository<String, Car>>(
                RepositoryReflect(connection, Car::class),
                loadDynamicRepo(connection, Car::class),
            )
    }

    private val jdbcInserter = CarDBInserterJdbc(connection)

    @ParameterizedTest
    @MethodSource("repositories")
    fun `getById should return the correct car`(repository: Repository<Long, Car>) {
        val carId = 1L
        val car: Car? = repository.getById(carId)
        assertEquals(carId, car?.id)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `getById should return null for non-existent car`(repository: Repository<Long, Car>) {
        val carId = 9999L
        val car: Car? = repository.getById(carId)
        assertNull(car)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `getAll should return all cars`(repository: Repository<Long, Car>) {
        val cars: List<Car> = repository.getAll().filter { it.id in 1..5 }
        assertEquals(5, cars.size)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `update should modify the car`(repository: Repository<Long, Car>) {
        val id = 1L
        val car: Car = repository.getAll().first { it.id == id }
        val newBrand = "Updated Brand"
        val updatedCar = car.copy(brand = newBrand)
        repository.update(updatedCar)

        val retrievedCar: Car = repository.getAll().first { it.id == id }
        assertEquals(newBrand, retrievedCar.brand)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `deleteById should remove the car`(repository: Repository<Long, Car>) {
        val newCar =
            Car(
                id = 20L,
                brand = "Test Brand",
                year = 2023,
                Engine(20L, "Test Engine", 2000, Fuel.DIESEL),
                Transmission(20L, "Test Transmission", TransmissionType.MANUAL, 6),
            )

        try {
            jdbcInserter.insertCar(newCar)
        } catch (_: PSQLException) {
            println("Car with id ${newCar.id} already exists, skipping insert.")
        }

        repository.deleteById(newCar.id)

        val retrievedCar: Car? = repository.getAll().firstOrNull { it.id == newCar.id }
        assertNull(retrievedCar)
    }
}
