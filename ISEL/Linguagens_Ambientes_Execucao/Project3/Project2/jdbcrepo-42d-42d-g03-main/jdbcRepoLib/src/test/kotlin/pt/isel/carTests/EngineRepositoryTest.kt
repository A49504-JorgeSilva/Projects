package pt.isel.carTests

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import pt.isel.Repository
import pt.isel.RepositoryReflect
import pt.isel.car.Engine
import pt.isel.car.Fuel
import pt.isel.car.dao.CarDBInserterJdbc
import pt.isel.car.dao.EngineRepositoryJdbc
import pt.isel.loadDynamicRepo
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EngineRepositoryTest {
    companion object {
        private val connection: Connection = DriverManager.getConnection(CAR_DB_URL)

        @JvmStatic
        fun repositories(): List<Repository<Long, Engine>> {
            return listOf(
                RepositoryReflect(connection, Engine::class),
                loadDynamicRepo(connection, Engine::class),
            )
        }
    }

    private val jdbcInserter = CarDBInserterJdbc(connection)

    @BeforeEach
    fun setup() {
        EngineRepositoryJdbc(connection).run {
            deleteById("22")
        }
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `getById should return the correct engine`(repository: Repository<Long, Engine>) {
        val engineId = 1L
        val engine: Engine? = repository.getById(engineId)
        assertEquals(engineId, engine?.id)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `getById should return null for non-existent engine`(repository: Repository<Long, Engine>) {
        val engineId = 9999L
        val engine: Engine? = repository.getById(engineId)
        assertNull(engine)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `getAll should return all engines`(repository: Repository<Long, Engine>) {
        val engines: List<Engine> = repository.getAll().filter { it.id in 1..5 }
        assertEquals(5, engines.size)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `update should modify the engine`(repository: Repository<Long, Engine>) {
        val id = 1L
        val engine: Engine = repository.getAll().first { it.id == id }
        val newBrand = "Updated Brand"
        val updatedEngine = engine.copy(brand = newBrand)
        repository.update(updatedEngine)

        val retrievedEngine: Engine = repository.getAll().first { it.id == id }
        assertEquals(newBrand, retrievedEngine.brand)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `deleteById should remove the engine`(repository: Repository<Long, Engine>) {
        val newEngine =
            Engine(
                id = 21L,
                brand = "Test Brand",
                power = 200,
                fuel = Fuel.DIESEL,
            )

        jdbcInserter.insertEngine(newEngine)

        repository.deleteById(newEngine.id)

        val retrievedEngine: Engine? = repository.getAll().firstOrNull { it.id == newEngine.id }
        assertNull(retrievedEngine)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `test lazy query execution with orderBy`(repository: Repository<Long, Engine>) {
        val engines =
            repository
                .findAll()
                .whereEquals(Engine::fuel, Fuel.GASOLINE)
                .orderBy(Engine::power)
                .iterator()

        // Insert a new public and read-only channel before iterating over the result
        CarDBInserterJdbc(connection).insertEngine(
            Engine(
                id = 22L,
                brand = "Test Engine",
                power = 300,
                fuel = Fuel.GASOLINE,
            ),
        )

        // The newly inserted channel will appear during iteration, even though the query was defined earlier
        assertEquals("Honda i-VTEC", engines.next().brand)
        assertEquals("Toyota Hybrid Synergy Drive", engines.next().brand)
    }
}
