package pt.isel.pc.problemsets.set1

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.testng.Assert.assertTrue
import java.time.Duration
import kotlin.concurrent.thread
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BlockingMessageQueueTests {
    private var bmqueue = BlockingMessageQueue<Int>(5)

    @Test
    fun `tryEnqueue test`() {
        var x: Int? = null
        var y: Int? = null
        var z: Int? = null
        val th1 = Thread {
            x = bmqueue.tryDequeue(Duration.ofSeconds(50))
        }
        val th2 = Thread {
            y = bmqueue.tryDequeue(Duration.ofSeconds(50))
        }
        val th3 = Thread {
            z = bmqueue.tryDequeue(Duration.ofSeconds(50))
        }
        val th4 = Thread {
            bmqueue.tryEnqueue(listOf(1, 2, 3), Duration.ofSeconds(1))
        }
        th1.start()
        th2.start()
        th3.start()
        Thread.sleep(4000)
        th4.start()
        th1.join()
        th2.join()
        th3.join()
        th4.join()
        assertTrue(x != null)
        assertTrue(y != null)
        assertTrue(z != null)
    }

    @Test
    fun `tryEnqueue2 test`() {
        var x = false
        val th1 = Thread {
            x = bmqueue.tryEnqueue(listOf(1, 2, 3), Duration.ofSeconds(1))
        }
        th1.start()
        th1.join()
        assertTrue(x)
    }
}