package pt.isel.car.dao

import pt.isel.Queryable
import pt.isel.Repository
import pt.isel.car.Transmission
import pt.isel.car.TransmissionType
import java.sql.Connection
import java.sql.ResultSet

class TransmissionRepositoryJdbc(
    private val connection: Connection,
) : Repository<String, Transmission> {
    override fun getAll(): List<Transmission> {
        TODO("Not yet implemented")
    }

    override fun getById(id: String): Transmission? {
        val sql = "SELECT * FROM transmissions WHERE id = ?"

        return connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, id)
            val rs = stmt.executeQuery()
            return if (rs.next()) mapRowToTransmission(rs) else null
        }
    }

    override fun update(entity: Transmission) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: String) {
        TODO("Not yet implemented")
    }

    override fun findAll(): Queryable<Transmission> {
        TODO("Not yet implemented")
    }

    private fun mapRowToTransmission(rs: ResultSet): Transmission =
        Transmission(
            id = rs.getLong("id"),
            brand = rs.getString("brand"),
            type = TransmissionType.valueOf(rs.getString("type")),
            gears = rs.getInt("gears"),
        )
}
