package pt.isel.pc.problemsets.set1


import org.junit.jupiter.api.Assertions
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import kotlin.test.Test

private val log = LoggerFactory.getLogger(CountDownLatchTests::class.java)

class CountDownLatchTests {
    private val N_OF_THREADS = 10
    private val N_OF_REPS = 1_000_000

    val countDownSync = CountDownLatch(2)

    private fun threadMethod() {
        val name = Thread.currentThread().name
        log.info("Running on thread '{}'", name)
        log.info("'{}'-Before countDown method the count was '{}'",name, countDownSync.getCount())
        if (countDownSync.getCount().toInt() == 0) {
            log.info("'{}' waiting, count is '{}'", name, countDownSync.getCount() )
            countDownSync.await(20,TimeUnit.SECONDS)
            log.info("'{}' done waiting, count is '{}'", name, countDownSync.getCount())
        }
        countDownSync.countDown()
        log.info("'{}'-After countDown method the count was '{}'",name, countDownSync.getCount())
    }
    @Test
    fun `we can have multiple threads running the same method`() {
        val name = Thread.currentThread().name
        log.info("Starting test on thread '{}'", name)

        // We can create multiples threads referencing the same method
        val ths = listOf(
            Thread(::threadMethod),
            Thread(::threadMethod),
            Thread(::threadMethod)
        )
        ths.forEach { thread -> thread.start() }
        log.info("New threads started, waiting for them to end")

        ths.forEach { thread -> thread.join() }
        log.info("New threads ended, finishing test")
    }

    @org.junit.jupiter.api.Test
    fun `countDown reaching zero`() {
        val countDownLatch = CountDownLatch(N_OF_REPS * N_OF_THREADS)
        repeat(N_OF_THREADS) {
            Thread.ofPlatform().start {
                // note that this code runs in a different thread
                repeat(N_OF_REPS) {
                    countDownLatch.countDown()
                }
            }
        }
        countDownLatch.await()
        Assertions.assertEquals(0L, countDownLatch.getCount())
    }

}