package pt.isel

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import pt.isel.car.Car
import pt.isel.car.dao.CarRepositoryJdbc
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime) // Measure execution time per operation
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
open class CarRepositoryBench {
    private val repoJdbc = CarRepositoryJdbc(FakeConnection())

    private val repoReflect = RepositoryReflect<String, Car>(FakeConnection(), Car::class)

    private val repoDynamic = loadDynamicRepo<String, Car>(FakeConnection(), Car::class)

    @Benchmark
    fun benchRepositoryJdbcGetAllChannels(): List<Car> = repoJdbc.getAll()

    @Benchmark
    fun benchRepositoryReflectGetAllChannels(): List<Car> = repoReflect.getAll()

    @Benchmark
    fun benchRepositoryDynamicGetAllChannels(): List<Car> = repoDynamic.getAll()
}
