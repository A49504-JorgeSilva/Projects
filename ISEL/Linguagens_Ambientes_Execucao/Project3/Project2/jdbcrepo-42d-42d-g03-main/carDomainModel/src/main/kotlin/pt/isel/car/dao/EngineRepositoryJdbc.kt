package pt.isel.car.dao

import pt.isel.Queryable
import pt.isel.Repository
import pt.isel.car.Engine
import pt.isel.car.Fuel
import java.sql.Connection
import java.sql.ResultSet

class EngineRepositoryJdbc(
    private val connection: Connection,
) : Repository<String, Engine> {
    override fun getAll(): List<Engine> {
        TODO("Not yet implemented")
    }

    override fun getById(id: String): Engine? {
        val sql = "SELECT * FROM engines WHERE id = ?"

        return connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, id)
            val rs = stmt.executeQuery()
            return if (rs.next()) mapRowToEngine(rs) else null
        }
    }

    override fun update(entity: Engine) {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: String) {
        val idlong = id.toLong()
        val sql = "DELETE FROM engines WHERE id = ?"

        connection.prepareStatement(sql).use { stmt ->
            stmt.setLong(1, idlong)
            stmt.executeUpdate()
        }
    }

    override fun findAll(): Queryable<Engine> {
        TODO("Not yet implemented")
    }

    private fun mapRowToEngine(rs: ResultSet): Engine =
        Engine(
            id = rs.getLong("id"),
            brand = rs.getString("brand"),
            power = rs.getInt("power"),
            fuel = Fuel.valueOf(rs.getString("fuel")),
        )
}
