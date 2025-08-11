package pt.isel

import org.slf4j.LoggerFactory
import pt.isel.chat.Channel
import pt.isel.chat.ChannelType
import pt.isel.chat.Message
import pt.isel.chat.User
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.test.Test

private val logger = LoggerFactory.getLogger(ChatJdbcTest::class.java)

class ChatJdbcTest {
    @Test
    fun `test fetch all messages from a channel`() {
        // Database connection properties
        val url = System.getenv("DB_URL") ?: throw Exception("Missing env var DB_URL")
        val user = "dbuser"
        val password = "changeit"
        DriverManager.getConnection(url, user, password).use { conn ->
            MessageDAO(conn)
                .fetchMessagesFromChannel("Development")
                .forEach { logger.info("[${it.timestamp}] ${it.user.name}: ${it.content} (ID: ${it.id})") }
        }
    }
}

// DAO class for messages
class MessageDAO(
    private val connection: Connection,
) {
    fun fetchMessagesFromChannel(channelName: String): List<Message> {
        val messages = mutableListOf<Message>()

        val query =
            """
            SELECT m.id, m.content, m.timestamp,
                   u.id AS user_id, u.name AS user_name, u.email, u.birthdate,
                   c.name AS channel_name, c.type, 
                   c.created_at, c.is_archived, c.max_message_length, 
                   c.max_members, c.is_read_only, c.last_message_timestamp
            FROM messages m
            JOIN users u ON m.user_id = u.id
            JOIN channels c ON m.channel_name = c.name
            WHERE c.name = ?
            ORDER BY m.timestamp;
            """.trimIndent()

        connection.prepareStatement(query).use { pstmt ->
            pstmt.setString(1, channelName)

            pstmt.executeQuery().use { rs ->
                while (rs.next()) {
                    messages.add(mapRowToMessage(rs))
                }
            }
        }
        return messages
    }

    private fun mapRowToMessage(rs: ResultSet): Message {
        val channel =
            Channel(
                name = rs.getString("channel_name"),
                type = ChannelType.valueOf(rs.getString("type")),
                createdAt = rs.getLong("created_at"),
                isArchived = rs.getBoolean("is_archived"),
                maxMessageLength = rs.getInt("max_message_length"),
                maxMembers = rs.getInt("max_members"),
                isReadOnly = rs.getBoolean("is_read_only"),
                lastMessageTimestamp = rs.getLong("last_message_timestamp"),
            )

        val user =
            User(
                id = rs.getLong("user_id"),
                name = rs.getString("user_name"),
                email = rs.getString("email"),
                birthdate = rs.getDate("birthdate"),
            )

        return Message(
            id = rs.getLong("id"),
            content = rs.getString("content"),
            timestamp = rs.getLong("timestamp"),
            user = user,
            channel = channel,
        )
    }
}
