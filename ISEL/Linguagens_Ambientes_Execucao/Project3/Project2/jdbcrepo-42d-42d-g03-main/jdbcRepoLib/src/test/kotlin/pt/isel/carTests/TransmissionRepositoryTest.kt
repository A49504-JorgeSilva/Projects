package pt.isel.carTests

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import pt.isel.Repository
import pt.isel.RepositoryReflect
import pt.isel.car.Transmission
import pt.isel.car.TransmissionType
import pt.isel.car.dao.CarDBInserterJdbc
import pt.isel.loadDynamicRepo
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TransmissionRepositoryTest {
    companion object {
        private val connection: Connection = DriverManager.getConnection(CAR_DB_URL)

        @JvmStatic
        fun repositories(): List<Repository<Long, Transmission>> {
            return listOf(
                RepositoryReflect(connection, Transmission::class),
                loadDynamicRepo(connection, Transmission::class),
            )
        }
    }

    private val jdbcInserter = CarDBInserterJdbc(connection)

    @ParameterizedTest
    @MethodSource("repositories")
    fun `getById should return the correct transmission`(repository: Repository<Long, Transmission>) {
        val transmissionId = 1L
        val transmission: Transmission? = repository.getById(transmissionId)
        assertEquals(transmissionId, transmission?.id)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `getById should return null for non-existent transmission`(repository: Repository<Long, Transmission>) {
        val transmissionId = 9999L
        val transmission: Transmission? = repository.getById(transmissionId)
        assertNull(transmission)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `getAll should return all transmissions`(repository: Repository<Long, Transmission>) {
        val transmissions: List<Transmission> = repository.getAll().filter { it.id in 1..5 }
        assertEquals(5, transmissions.size)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `update should modify the transmission`(repository: Repository<Long, Transmission>) {
        val id = 1L
        val transmission: Transmission = repository.getAll().first { it.id == id }
        val newBrand = "Updated Brand"
        val updatedTransmission = transmission.copy(brand = newBrand)
        repository.update(updatedTransmission)

        val retrievedTransmission: Transmission = repository.getAll().first { it.id == id }
        assertEquals(newBrand, retrievedTransmission.brand)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `deleteById should remove the transmission`(repository: Repository<Long, Transmission>) {
        val newTransmission =
            Transmission(
                id = 22L,
                brand = "Test Brand",
                type = TransmissionType.MANUAL,
                gears = 6,
            )

        jdbcInserter.insertTransmission(newTransmission)

        repository.deleteById(newTransmission.id)

        val retrievedTransmission: Transmission? = repository.getAll().firstOrNull { it.id == newTransmission.id }
        assertNull(retrievedTransmission)
    }
}
