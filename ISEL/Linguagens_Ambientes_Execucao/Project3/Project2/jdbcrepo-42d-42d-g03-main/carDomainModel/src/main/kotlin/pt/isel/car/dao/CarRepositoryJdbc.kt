package pt.isel.car.dao

import pt.isel.Queryable
import pt.isel.Repository
import pt.isel.car.Car
import java.sql.Connection
import java.sql.ResultSet

class CarRepositoryJdbc(
    private val connection: Connection,
) : Repository<String, Car> {
    private val engineRepository = EngineRepositoryJdbc(connection)
    private val transmissionRepository = TransmissionRepositoryJdbc(connection)

    override fun getAll(): List<Car> {
        val sql = "SELECT * FROM cars"

        return connection.prepareStatement(sql).use { stmt ->
            stmt.executeQuery().use { rs ->
                val cars = mutableListOf<Car>()
                while (rs.next()) {
                    cars.add(mapRowToCar(rs))
                }
                cars
            }
        }
    }

    override fun getById(id: String): Car? {
        TODO("Not yet implemented")
    }

    override fun update(entity: Car) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: String) {
        TODO("Not yet implemented")
    }

    override fun findAll(): Queryable<Car> {
        TODO("Not yet implemented")
    }

    private fun mapRowToCar(rs: ResultSet): Car =
        Car(
            id = rs.getLong("id"),
            brand = rs.getString("brand"),
            year = rs.getInt("year"),
            engine =
                engineRepository.getById(
                    rs.getLong("engine_id").toString(),
                ) ?: throw IllegalStateException("Engine not found"),
            transmission =
                transmissionRepository.getById(
                    rs.getLong("transmission_id").toString(),
                ) ?: throw IllegalStateException("Transmission not found"),
        )
}
