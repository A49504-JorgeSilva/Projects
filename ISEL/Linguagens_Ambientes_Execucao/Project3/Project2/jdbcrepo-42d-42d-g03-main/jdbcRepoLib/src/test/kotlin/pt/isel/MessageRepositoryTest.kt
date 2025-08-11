package pt.isel

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import pt.isel.chat.Message
import pt.isel.chat.dao.ChannelRepositoryJdbc
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement.RETURN_GENERATED_KEYS
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class MessageRepositoryTest {
    private val connection: Connection = DriverManager.getConnection(DB_URL)

    companion object {
        @JvmStatic
        fun repositories(): List<Repository<Long, Message>> {
            val connection = DriverManager.getConnection(DB_URL)
            return listOf(
                RepositoryReflect(connection, Message::class),
                loadDynamicRepo(connection, Message::class),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `getAll should return all messages`(repository: Repository<Long, Message>) {
        val messages: List<Message> = repository.getAll()
        assertTrue(messages.size >= 20)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `retrieve a message`(repository: Repository<Long, Message>) {
        val esportsMsg = repository.getAll().first { it.channel.name == "Esports Discussion" }
        val otherMsg = repository.getById(esportsMsg.id)
        assertNotNull(otherMsg)
        assertEquals(otherMsg, esportsMsg)
        assertNotSame(otherMsg, esportsMsg)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `retrieve message with PK three`(repository: Repository<Long, Message>) {
        val msg = repository.getById(3)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `update a message content`(repository: Repository<Long, Message>) {
        val devMsg = repository.getAll().first { it.channel.name == "Development" }
        val updatedMsg = devMsg.copy(content = "Only include relevant and reliable contents.")
        repository.update(updatedMsg)
        val retrieved = repository.getById(updatedMsg.id)
        assertNotNull(retrieved)
        assertEquals(updatedMsg, retrieved)
        assertNotSame(updatedMsg, retrieved)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `update a message channel`(repository: Repository<Long, Message>) {
        val devMsg = repository.getAll().first { it.channel.name == "Development" }
        val general = ChannelRepositoryJdbc(connection).getById("General")
        assertNotNull(general)
        val updatedMsg = devMsg.copy(channel = general)
        repository.update(updatedMsg)
        val retrieved = repository.getById(updatedMsg.id)
        assertNotNull(retrieved)
        assertEquals(updatedMsg, retrieved)
        assertNotSame(updatedMsg, retrieved)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `delete a message`(repository: Repository<Long, Message>) {
        val sql =
            """
            INSERT INTO messages (content, timestamp, user_id, channel_name)
            VALUES (?, ?, ?, ?)
            """
        val values =
            arrayOf<Any>(
                "With Reflection we can provide an auto implementation of SQL operations via JDBC.",
                LocalDate.now().toEpochDay(),
                2,
                "Development",
            )
        val pk =
            connection
                .prepareStatement(sql, RETURN_GENERATED_KEYS)
                .use { stmt ->
                    values.forEachIndexed { index, it -> stmt.setObject(index + 1, it) }
                    stmt.executeUpdate() // Executes the INSERT
                    stmt.generatedKeys.use { rs ->
                        rs.next()
                        rs.getLong(1)
                    }
                }
        assertEquals(21, repository.getAll().size)
        repository.deleteById(pk)
        assertEquals(20, repository.getAll().size)
    }
}
