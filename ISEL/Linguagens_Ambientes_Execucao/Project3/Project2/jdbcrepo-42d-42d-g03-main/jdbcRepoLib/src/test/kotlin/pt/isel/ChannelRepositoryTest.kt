package pt.isel

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import pt.isel.chat.Channel
import pt.isel.chat.ChannelType
import pt.isel.chat.dao.ChannelRepositoryJdbc
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

val DB_URL = System.getenv("DB_URL") ?: throw Exception("Missing env var DB_URL")

class ChannelRepositoryTest {
    private val channelRandom =
        Channel("Random", ChannelType.PRIVATE, System.currentTimeMillis(), false, 400, 50, false, 0L)

    companion object {
        private val connection: Connection = DriverManager.getConnection(DB_URL)

        @JvmStatic
        fun repositories() =
            listOf<Repository<String, Channel>>(
                RepositoryReflect(connection, Channel::class),
                loadDynamicRepo(connection, Channel::class),
            )
    }

    @BeforeEach
    fun setup() {
        ChannelRepositoryJdbc(connection).run {
            deleteById("Random")
            deleteById("Surf")
            insert(channelRandom)
        }
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `retrieve a channel`(repository: Repository<String, Channel>) {
        val retrieved = repository.getById("General")
        assertNotNull(retrieved)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `update a channel`(repository: Repository<String, Channel>) {
        val updatedChannel = channelRandom.copy(maxMembers = 200, isReadOnly = true)
        repository.update(updatedChannel)

        val retrieved = repository.getById(channelRandom.name)
        assertNotNull(retrieved)
        assertEquals(200, retrieved.maxMembers)
        assertEquals(true, retrieved.isReadOnly)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `delete a channel`(repository: Repository<String, Channel>) {
        repository.deleteById(channelRandom.name)
        val retrieved = repository.getById(channelRandom.name)
        assertNull(retrieved)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `getAll should return all channels`(repository: Repository<String, Channel>) {
        val channels: List<Channel> = repository.getAll()
        assertEquals(6, channels.size)
        assertTrue(channelRandom in channels)
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `test lazy query execution`(repository: Repository<String, Channel>) {
        val channelsPublicAndReadOnly =
            repository
                .findAll()
                .whereEquals(Channel::type, ChannelType.PUBLIC)
                .whereEquals(Channel::isReadOnly, true)
                .iterator()

        // Insert a new public and read-only channel before iterating over the result
        ChannelRepositoryJdbc(connection).insert(
            Channel("Surf", ChannelType.PUBLIC, System.currentTimeMillis(), false, 400, 50, true, 0L),
        )

        // The newly inserted channel will appear during iteration, even though the query was defined earlier
        assertEquals("Support", channelsPublicAndReadOnly.next().name)
        assertEquals("Surf", channelsPublicAndReadOnly.next().name)
        assertFalse { channelsPublicAndReadOnly.hasNext() }
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `test lazy query execution with orderBy`(repository: Repository<String, Channel>) {
        val channelsPublicAndReadOnly =
            repository
                .findAll()
                .orderBy(Channel::name)
                .orderBy(Channel::type)
                .iterator()

        // Insert a new public and read-only channel before iterating over the result
        ChannelRepositoryJdbc(connection).insert(
            Channel("Surf", ChannelType.PUBLIC, System.currentTimeMillis(), false, 400, 50, true, 0L),
        )

        // The newly inserted channel will appear during iteration, even though the query was defined earlier
        assertEquals("Development", channelsPublicAndReadOnly.next().name)
        assertEquals("Esports Discussion", channelsPublicAndReadOnly.next().name)
    }
}
