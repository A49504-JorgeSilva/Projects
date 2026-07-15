package pt.isel.ls.houses.data.postgres

import pt.isel.ls.houses.data.BookingRepository
import pt.isel.ls.houses.domain.Booking
import java.sql.Date
import java.sql.ResultSet
import java.sql.Statement
import java.time.LocalDate
import javax.sql.DataSource

class BookingRepositoryPostgres(private val dataSource: DataSource) : BookingRepository {

    override fun create(houseId: Int, userId: Int, startDate: LocalDate, endDate: LocalDate): Booking {
        val sql = "INSERT INTO bookings (house_id, user_id, start_date, end_date) VALUES (?, ?, ?, ?)"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { stm ->
                stm.setInt(1, houseId)
                stm.setInt(2, userId)
                stm.setDate(3, Date.valueOf(startDate))
                stm.setDate(4, Date.valueOf(endDate))

                stm.executeUpdate()

                val rs = stm.generatedKeys
                if (rs.next()) {
                    val id = rs.getInt(1)
                    return Booking(id, startDate, endDate, userId, houseId)
                } else {
                    throw IllegalStateException("Failed to retrieve generated ID")
                }
            }
        }
    }

    override fun getById(id: Int): Booking? {
        val sql = "SELECT id, house_id, user_id, start_date, end_date FROM bookings WHERE id = ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setInt(1, id)
                val rs = stm.executeQuery()
                if (rs.next()) {
                    return Booking(
                        id = rs.getInt("id"),
                        startDate = rs.getDate("start_date").toLocalDate(),
                        endDate = rs.getDate("end_date").toLocalDate(),
                        userId = rs.getInt("user_id"),
                        houseId = rs.getInt("house_id")
                    )
                }
                return null
            }
        }
    }

    override fun getOverlappingBookings(houseId: Int, start: LocalDate, end: LocalDate): List<Booking> {
        val bookings = mutableListOf<Booking>()
        val sql = "SELECT * FROM bookings WHERE house_id = ? AND start_date < ? AND end_date > ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setInt(1, houseId)
                stm.setDate(2, Date.valueOf(end))
                stm.setDate(3, Date.valueOf(start))
                val rs = stm.executeQuery()
                while (rs.next()) {
                    bookings.add(
                        Booking(
                            id = rs.getInt("id"),
                            startDate = rs.getDate("start_date").toLocalDate(),
                            endDate = rs.getDate("end_date").toLocalDate(),
                            userId = rs.getInt("user_id"),
                            houseId = rs.getInt("house_id")
                        )
                    )
                }
            }
        }
        return bookings
    }

    override fun getBookingsByHouseAndDates(houseId: Int, start: LocalDate, end: LocalDate, skip: Int, limit: Int): List<Booking> {
        val bookings = mutableListOf<Booking>()
        val sql = "SELECT * FROM bookings WHERE house_id = ? AND start_date < ? AND end_date > ? ORDER BY start_date OFFSET ? LIMIT ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setInt(1, houseId)
                stm.setDate(2, Date.valueOf(end))
                stm.setDate(3, Date.valueOf(start))
                stm.setInt(4, skip)
                stm.setInt(5, limit)
                val rs = stm.executeQuery()
                while (rs.next()) {
                    bookings.add(
                        Booking(
                            id = rs.getInt("id"),
                            startDate = rs.getDate("start_date").toLocalDate(),
                            endDate = rs.getDate("end_date").toLocalDate(),
                            userId = rs.getInt("user_id"),
                            houseId = rs.getInt("house_id")
                        )
                    )
                }
            }
        }
        return bookings
    }

    override fun getBookedHouseIds(start: LocalDate, end: LocalDate): Set<Int> {
        val houseIds = mutableSetOf<Int>()
        val sql = "SELECT house_id FROM bookings WHERE start_date < ? AND end_date > ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setDate(1, Date.valueOf(end))
                stm.setDate(2, Date.valueOf(start))
                val rs = stm.executeQuery()
                while (rs.next()) {
                    houseIds.add(rs.getInt("house_id"))
                }
            }
        }
        return houseIds
    }

    override fun getByUserId(userId: Int, skip: Int, limit: Int): List<Booking>{
        val bookings = mutableListOf<Booking>()
        val sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY start_date OFFSET ? LIMIT ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setInt(1, userId)
                stm.setInt(2, skip)
                stm.setInt(3, limit)
                val rs = stm.executeQuery()
                while (rs.next()) bookings.add(mapRow(rs))
            }
        }
        return bookings
    }

    override fun deleteBooking(id: Int): Boolean {
        val sql = "DELETE FROM bookings WHERE id = ?"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setInt(1, id)
                return stm.executeUpdate() > 0
            }
        }
    }

    override fun updateBooking(id: Int, startDate: LocalDate, endDate: LocalDate): Booking? {
        val sql = "UPDATE bookings SET start_date = ?, end_date = ? WHERE id = ? RETURNING *"
        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stm ->
                stm.setDate(1, Date.valueOf(startDate))
                stm.setDate(2, Date.valueOf(endDate))
                stm.setInt(3, id)
                val rs = stm.executeQuery()
                return if (rs.next()) mapRow(rs) else null
            }
        }
    }

    // Maps a ResultSet row to a Booking domain object
    private fun mapRow(rs: ResultSet) = Booking(
        id = rs.getInt("id"),
        startDate = rs.getDate("start_date").toLocalDate(),
        endDate = rs.getDate("end_date").toLocalDate(),
        userId = rs.getInt("user_id"),
        houseId = rs.getInt("house_id")
    )
}