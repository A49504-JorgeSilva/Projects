package pt.isel

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import pt.isel.chat.Channel
import pt.isel.chat.dao.ChannelRepositoryJdbc
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime) // Measure execution time per operation
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
open class RepositoryBench {
    private val repoJdbc = ChannelRepositoryJdbc(FakeConnection())

    private val repoReflect = RepositoryReflect<String, Channel>(FakeConnection(), Channel::class)

    private val repoDynamic = loadDynamicRepo<String, Channel>(FakeConnection(), Channel::class)

    @Benchmark
    fun benchRepositoryJdbcGetAllChannels(): List<Channel> = repoJdbc.getAll()

    @Benchmark
    fun benchRepositoryReflectGetAllChannels(): List<Channel> = repoReflect.getAll()

    @Benchmark
    fun benchRepositoryDynamicGetAllChannels(): List<Channel> = repoDynamic.getAll()
}
