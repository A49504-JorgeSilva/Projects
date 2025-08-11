package pt.isel

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import pt.isel.chat.Channel
import kotlin.test.assertEquals

class RepositoryReflectTest {
    companion object {
        @JvmStatic
        fun repositories(): List<Repository<String, Channel>> =
            listOf(
                RepositoryReflect(FakeConnection(), Channel::class),
                loadDynamicRepo(FakeConnection(), Channel::class),
            )
    }

    @ParameterizedTest
    @MethodSource("repositories")
    fun `getAll should return all channels`(repository: Repository<String, Channel>) {
        val channels: List<Channel> = repository.getAll()
        assertEquals(5, channels.size)
    }
}
