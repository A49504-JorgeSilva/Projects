package pt.isel.ls.houses.data.postgres

import pt.isel.ls.houses.data.HouseRepository
import pt.isel.ls.houses.domain.House
import pt.isel.ls.houses.domain.LocationType
import java.sql.Statement
import javax.sql.DataSource

class HouseRepositoryPostgres(private val dataSource: DataSource) : HouseRepository {
    override fun createHouse(title: String, locationId: Int, area: Double, price: Double, description: String, ownerId: Int): Int {
        val sql = "INSERT INTO houses (title, location_id, area_sqmt, price_per_night, description, owner_id) VALUES (?, ?, ?, ?, ?, ?)"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stm ->
                stm.setString(1, title)
                stm.setInt(2, locationId)
                stm.setDouble(3, area)
                stm.setDouble(4, price)
                stm.setString(5, description)
                stm.setInt(6, ownerId)

                stm.executeUpdate()

                val rs = stm.generatedKeys
                if (rs.next()) {
                    val id = rs.getInt(1)
                    return id
                } else {
                    throw IllegalStateException("Failed to retrieve generated ID")
                }
            }
        }
    }

    override fun getHouseById(id: Int): House? {
        val sql = "SELECT * FROM houses WHERE id = ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setInt(1, id)
                val rs = stm.executeQuery()
                if (rs.next()) {
                    return House(
                        id = rs.getInt("id"),
                        title = rs.getString("title"),
                        locationId = rs.getInt("location_id"),
                        areaSqMt = rs.getDouble("area_sqmt"),
                        pricePerNight = rs.getDouble("price_per_night"),
                        description = rs.getString("description"),
                        ownerId = rs.getInt("owner_id"),
                    )
                }
                return null
            }
        }
    }

    override fun getAllHouses(searchTitle: String?, searchLocation: String?, skip: Int, limit: Int): List<House> {
        val conditions = mutableListOf<String>()
        if (searchTitle != null) conditions.add("LOWER(h.title) LIKE LOWER(?)")
        if (searchLocation != null) conditions.add("LOWER(l.name) LIKE LOWER(?)")

        val whereClause = if (conditions.isEmpty()) "" else "WHERE ${conditions.joinToString(" AND ")}"
        val sql = "SELECT h.* FROM houses h LEFT JOIN locations l ON h.location_id = l.id $whereClause OFFSET ? LIMIT ?"

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                var idx = 1
                if (searchTitle != null) stm.setString(idx++, "%$searchTitle%")
                if (searchLocation != null) stm.setString(idx++, "%$searchLocation%")
                stm.setInt(idx++, skip)
                stm.setInt(idx, limit)
                val rs = stm.executeQuery()
                val houses = mutableListOf<House>()
                while (rs.next()) {
                    houses.add(
                        House(
                            id = rs.getInt("id"),
                            title = rs.getString("title"),
                            locationId = rs.getInt("location_id"),
                            areaSqMt = rs.getDouble("area_sqmt"),
                            pricePerNight = rs.getDouble("price_per_night"),
                            description = rs.getString("description"),
                            ownerId = rs.getInt("owner_id"),
                        )
                    )
                }
                return houses
            }
        }
    }
}