package pt.isel.chat.dao

import pt.isel.Queryable
import pt.isel.chat.Channel
import pt.isel.chat.ChannelType
import java.sql.Connection
import java.sql.ResultSet

class ChannelRepositoryJdbc(
    private val connection: Connection,
) : ChannelRepository {
    override fun insert(
        name: String,
        type: ChannelType,
        createdAt: Long,
        isArchived: Boolean,
        maxMessageLength: Int,
        maxMembers: Int,
        isReadOnly: Boolean,
        lastMessageTimestamp: Long,
    ) {
        val existsSql = "SELECT 1 FROM channels WHERE name = ?"
        val sql =
            """
            INSERT INTO channels (name, type, created_at, is_archived, max_message_length, 
                                  max_members, is_read_only, last_message_timestamp) 
            VALUES (?, ?::channel_type, ?, ?, ?, ?, ?, ?)
            """.trimIndent()

        connection.prepareStatement(existsSql).use { stmt ->
            stmt.setString(1, name)
            val rs = stmt.executeQuery()
            if (rs.next()) {
                return
            }
        }

        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, name)
            stmt.setString(2, type.name)
            stmt.setLong(3, createdAt)
            stmt.setBoolean(4, isArchived)
            stmt.setInt(5, maxMessageLength)
            stmt.setInt(6, maxMembers)
            stmt.setBoolean(7, isReadOnly)
            stmt.setObject(8, lastMessageTimestamp) // Use setObject for nullable LONG
            stmt.executeUpdate()
        }
    }

    override fun getById(id: String): Channel? {
        val sql = "SELECT * FROM channels WHERE name = ?"

        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, id)
            val rs = stmt.executeQuery()
            return if (rs.next()) mapRowToChannel(rs) else null
        }
    }

    override fun getAll(): List<Channel> {
        val sql = "SELECT * FROM channels"
        return connection.prepareStatement(sql).use { stmt ->
            stmt.executeQuery().use { rs ->
                val channels = mutableListOf<Channel>()
                while (rs.next()) {
                    channels.add(mapRowToChannel(rs))
                }
                channels
            }
        }
    }

    override fun update(entity: Channel) {
        val sql =
            """
            UPDATE channels 
            SET type = ?::channel_type, created_at = ?, is_archived = ?, 
                max_message_length = ?, max_members = ?, is_read_only = ?, 
                last_message_timestamp = ? 
            WHERE name = ?
            """.trimIndent()

        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, entity.type.toString())
            stmt.setLong(2, entity.createdAt)
            stmt.setBoolean(3, entity.isArchived)
            stmt.setInt(4, entity.maxMessageLength)
            stmt.setInt(5, entity.maxMembers)
            stmt.setBoolean(6, entity.isReadOnly)
            stmt.setObject(7, entity.lastMessageTimestamp)
            stmt.setString(8, entity.name)
            stmt.executeUpdate()
        }
    }

    override fun deleteById(id: String) {
        val sql = "DELETE FROM channels WHERE name = ?"

        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, id)
            stmt.executeUpdate()
        }
    }

    override fun findAll(): Queryable<Channel> {
        TODO()
    }

    private fun mapRowToChannel(rs: ResultSet): Channel =
        Channel(
            name = rs.getString("name"),
            type = ChannelType.valueOf(rs.getString("type")),
            createdAt = rs.getLong("created_at"),
            isArchived = rs.getBoolean("is_archived"),
            maxMessageLength = rs.getInt("max_message_length"),
            maxMembers = rs.getInt("max_members"),
            isReadOnly = rs.getBoolean("is_read_only"),
            lastMessageTimestamp = rs.getLong("last_message_timestamp"),
        )
}
