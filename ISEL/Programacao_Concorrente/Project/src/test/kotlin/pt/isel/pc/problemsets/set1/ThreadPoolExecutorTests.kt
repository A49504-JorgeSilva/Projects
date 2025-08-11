package pt.isel.pc.problemsets.set1

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicInteger


private val log = LoggerFactory.getLogger(ThreadPoolExecutorTests::class.java)

class ThreadPoolExecutorTests {
    private fun threadMethod(i: Int) {
        val name = Thread.currentThread().name
        log.info("Running on thread '{}'", name)
        Thread.sleep(TimeUnit.SECONDS.toMillis(2))
    }

    @Test
    fun `execute`() {
        val executor = ThreadPoolExecutor(2, Duration.ofSeconds(50))
        val runnable = Runnable { println("Teste 1") }

        executor.execute(runnable)
        println("Thread started")

        assertEquals(1, executor.threadsCount)
    }



    @Test
    fun `working threads waiting`() {
        val executor = ThreadPoolExecutor(2, Duration.ofSeconds(20))
        val th = Thread {
            val run = Runnable { println("Testing Executor") }
            val run2 = Runnable { println("Testing Executor 2") }
            executor.execute(run)
            executor.execute(run2)
            Thread.sleep(5000)
            assertEquals(2,executor.waitingCount)
        }

        th.start()
        th.join()
    }

    @Test
    fun `shutdown test`() {
        val executor = ThreadPoolExecutor(1, Duration.ofSeconds(20))
        val th = Thread{
            val run = Runnable { println("Testing Executor") }
            val run2 = Runnable {
                Thread.sleep(3000)
                println("Testing Executor 2") }
            val run3 = Runnable { println("Testing Executor 3") }

            executor.execute(run)
            Thread.sleep(2000)
            executor.execute(run2)
            executor.shutdown()
            assertEquals(true, executor.shuttingFlag)
            assertThrows<RejectedExecutionException> {
                executor.execute(run3)
            }
        }

        th.start()
        th.join()
    }

    @Test
    fun `keepAliveTime test`(){
        val keepAliveTime = Duration.ofSeconds(2)
        val executor = ThreadPoolExecutor(2, keepAliveTime)
        val th = Thread{
            val run = Runnable { println("Testing Executor") }
            executor.execute(run)
            assertEquals(1,executor.threadsCount)
            Thread.sleep(keepAliveTime.toMillis() + 100)
            assertEquals(0, executor.threadsCount)
        }

        th.start()
        th.join()
    }

    @Test
    fun `awaitTermination success`(){
        val executor = ThreadPoolExecutor(2, Duration.ofSeconds(50))
        val th =  Thread{
            val run = Runnable { println("Testing Executor") }
            executor.execute(run)
            executor.shutdown()
            assertTrue(executor.awaitTermination(Duration.ofSeconds(5)))
        }

        th.start()
        th.join()
    }
    /*@Test
    fun `awaitTermination timeout`(){
        val executor = ThreadPoolExecutor(1, Duration.ofSeconds(50))
        val th =  Thread{
            val run = Runnable { println("Testing Executor on th1") }
            executor.execute(run)
            Thread.sleep(500)
            executor.shutdown()
            Thread.sleep(2000)
            assertFalse(executor.awaitTermination(Duration.ofMillis(100)))
        }

        val th2 = Thread{
            val run = Runnable { println("Testing Executor on th2") }
            val run2 = Runnable { println("Testing2 Executor on th2") }
            executor.execute(run)
            Thread.sleep(1000)
            executor.execute(run2)
        }

        th.start()
        th2.start()
        th.join()
        th2.join()
    }*/

    @Test
    fun`InterruptedException after awaitTermination`(){
        val executor = ThreadPoolExecutor(1, Duration.ofSeconds(50))
        val th =  Thread{
            val run = Runnable { println("Testing Executor on th1") }
            executor.execute(run)
            Thread.sleep(500)
            executor.shutdown()
            Thread.sleep(2000)
        }

        val th2 = Thread{
            val run = Runnable { println("Testing Executor on th2") }
            val run2 = Runnable { println("Testing2 Executor on th2") }
            executor.execute(run)
            Thread.sleep(1000)
            assertThrows<RejectedExecutionException> {
                executor.execute(run2)
            }
        }

        th.start()
        th2.start()
        th.join()
        th2.join()
    }
}
