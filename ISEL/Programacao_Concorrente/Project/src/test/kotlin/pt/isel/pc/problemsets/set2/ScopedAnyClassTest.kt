package pt.isel.pc.problemsets.set2

import org.junit.jupiter.api.Assertions.assertThrows
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail

class ScopedAnyClassTest {

    @Test
    fun `success test`(){
        val scopedAnyClass = ScopedAnyClass<Int>()
        val future1 = CompletableFuture.completedFuture(1)
        val future2 = CompletableFuture.completedFuture(2)
        val futures = listOf(future1, future2)

        val resultFuture = CompletableFuture<Int>()

        Thread {
            scopedAnyClass.scopedAny(futures) { value ->
                resultFuture.complete(value)
            }
        }.start()

        Thread.sleep(1000)

        assertTrue(resultFuture.isDone)
        assertEquals(1, resultFuture.get())
    }


    @Test
    fun `failure test`() {
        val scopedAnyClass = ScopedAnyClass<String>()

        val future1 = CompletableFuture.failedFuture<String>(RuntimeException("Future 1 failed"))
        val future2 = CompletableFuture.failedFuture<String>(RuntimeException("Future 2 failed"))
        val future3 = CompletableFuture.failedFuture<String>(RuntimeException("Future 3 failed"))

        val futures = listOf(future1, future2, future3)

        val result = scopedAnyClass.scopedAny(futures) {}

        assertFailsWith<ExecutionException> {
            result.get()
        }.let { e ->
            assertTrue(e.cause is ScopedAnyClass.AllFutureFailedClass)
            val cause = e.cause as ScopedAnyClass.AllFutureFailedClass
            assertEquals("All futures failed", cause.message)
            assertEquals(3, cause.suppressed.size)
        }
    }

    @Test
    fun `one success case`() {
        val scopedAnyClass = ScopedAnyClass<Int>()
        val future1 = CompletableFuture.supplyAsync {
            Thread.sleep(2000)
            "Result 1"
        }

        val future2 = CompletableFuture.supplyAsync<String> {
            Thread.sleep(1000)
            throw RuntimeException("Exception in Future 2")
        }

        val future3 = CompletableFuture.supplyAsync {
            Thread.sleep(3000)
            "Result 3"
        }

        val futures = listOf(future1, future2, future3)

        val resultFuture = scopedAnyClass.scopedAny(futures) { result ->
            println("First success: $result")
        }

        resultFuture.whenComplete { result, exception ->
            if (exception != null) {
                println("Exception: ${exception.message}")
            } else {
                println("Result: $result")
            }
        }

        assertEquals("Result 1", resultFuture.join())

    }

}