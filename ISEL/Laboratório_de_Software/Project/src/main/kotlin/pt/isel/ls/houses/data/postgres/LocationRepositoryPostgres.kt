package pt.isel.ls.houses.data.postgres

import pt.isel.ls.houses.data.LocationRepository
import pt.isel.ls.houses.domain.Location
import pt.isel.ls.houses.domain.LocationType
import javax.sql.DataSource
import java.sql.ResultSet
import java.sql.Statement

class LocationRepositoryPostgres(private val dataSource: DataSource) : LocationRepository {

    override fun create(name: String, type: LocationType, parentId: Int?): Location {
        val sql = "INSERT INTO locations (name, type, parent_id) VALUES (?, ?, ?)"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stm ->
                stm.setString(1, name)
                stm.setString(2, type.name) // guarda o nome do enum como ‘string’
                if (parentId != null) {
                    stm.setInt(3, parentId)
                } else {
                    stm.setNull(3, java.sql.Types.INTEGER)
                }
                stm.executeUpdate()

                val rs = stm.generatedKeys
                if (rs.next()) {
                    val id = rs.getInt(1)
                    return Location(id, name, type, parentId)
                } else {
                    throw IllegalStateException("Failed to retrieve generated ID for location")
                }
            }
        }
    }

    override fun getById(id: Int): Location? {
        val sql = "SELECT id, name, type, parent_id FROM locations WHERE id = ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setInt(1, id)
                val rs = stm.executeQuery()
                return if (rs.next()) mapRow(rs) else null
            }
        }
    }

    override fun getByName(name: String): Location? {
        val sql = "SELECT id, name, type, parent_id FROM locations WHERE name = ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setString(1, name)
                val rs = stm.executeQuery()
                return if (rs.next()) mapRow(rs) else null
            }
        }
    }

    override fun getByPartialName(name: String): List<Location> {
        val sql = "SELECT id, name, type, parent_id FROM locations WHERE name LIKE CONCAT('%', ?, '%')"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setString(1, name)
                val rs = stm.executeQuery()
                val locations = mutableListOf<Location>()
                while (rs.next()) {
                    locations.add(mapRow(rs))
                }
                return locations
            }
        }
    }

    override fun getByParentId(parentId: Int): List<Location> {
        val sql = "SELECT id, name, type, parent_id FROM locations WHERE parent_id = ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setInt(1, parentId)
                val rs = stm.executeQuery()
                val locations = mutableListOf<Location>()
                while (rs.next()) {
                    locations.add(mapRow(rs))
                }
                return locations
            }
        }
    }

    override fun getAll(type: LocationType?): List<Location> {
        val sql = if (type != null) {
            "SELECT id, name, type, parent_id FROM locations WHERE type = ?"
        } else {
            "SELECT id, name, type, parent_id FROM locations"
        }
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                if (type != null) {
                    stm.setString(1, type.name)
                }
                val rs = stm.executeQuery()
                val locations = mutableListOf<Location>()
                while (rs.next()) {
                    locations.add(mapRow(rs))
                }
                return locations
            }
        }
    }

    private fun mapRow(rs: ResultSet): Location {
        val id = rs.getInt("id")
        val name = rs.getString("name")
        val type = LocationType.valueOf(rs.getString("type")) // converte string de volta para enum
        val parentId = rs.getInt("parent_id").takeIf { !rs.wasNull() } // se for NULL, parentId fica null
        return Location(id, name, type, parentId)
    }
}