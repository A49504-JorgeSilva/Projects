package pt.isel.pc.problemsets.set1

import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertTrue

class ExampleTest {
    private var bmqueue = BlockingMessageQueue<Int>(10)

    @Test
    fun `the test`() {
        assertTrue(true)
    }

    @Test
    fun `tryEnqueue test`() {
        assertTrue(bmqueue.tryEnqueue(listOf(1, 2, 3), Duration.ofSeconds(1)))
    }

}