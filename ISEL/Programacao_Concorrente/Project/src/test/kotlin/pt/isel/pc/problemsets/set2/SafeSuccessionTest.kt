
package pt.isel.pc.problemsets.set2

import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SafeSuccessionTest {

    @Test
    fun `test list`(){
        var x: Int? = null
        var y: Int? = null
        var z: Int? = null
        var n: Int? = null
        val items = arrayOf(1, 2, 3)
        val succession = SafeSuccession(items)
        val th1 = Thread {
            x = succession.next()
        }
        val th2 = Thread {
            y = succession.next()
        }
        val th3 = Thread {
            z = succession.next()
        }
        val th4 = Thread {
            n = succession.next()
        }
        th1.start()
        th2.start()
        th3.start()
        th1.join()
        th2.join()
        th3.join()
        assertTrue(x == 1)
        assertTrue(y == 2)
        assertTrue(z == 3)
        th4.start()
        th4.join()
        assertTrue { n == null }

    }

    @Test
    fun `test empty list`(){
        var x: Int? = null
        val items = arrayOf<Int>()
        val succession = SafeSuccession(items)
        val th1 = Thread {
            x = succession.next()
        }
        th1.start()
        th1.join()
        assertTrue { x == null }
    }

    @Test
    fun `test list with one element`(){
        var x: Int? = null
        var y: Int? = null
        val items = arrayOf(1)
        val succession = SafeSuccession(items)
        val th1 = Thread {
            x = succession.next()
        }
        val th2 = Thread {
            y = succession.next()
        }
        th1.start()
        th1.join()
        assertTrue(x == 1)
        th2.start()
        th2.join()
        assertTrue { y == null }
    }

    @Test
    fun `test concurrence`(){
        val items = arrayOf(1, 2, 3, 4, 5)
        val succession = SafeSuccession(items)

        val executor = Executors.newFixedThreadPool(5)
        val latch = CountDownLatch(5)
        val results = mutableListOf<Int?>()

        repeat(5) {
            executor.submit {
                val result = succession.next()
                results.add(result)
                latch.countDown()
            }
        }
        latch.await()

        assertEquals(5, results.size)
        assertTrue(results.containsAll(items.toList()))
    }
}
