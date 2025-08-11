package pt.isel.pc.problemsets.set1

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Kernel Style
 */
class CountDownLatch(
    initialCount: Int,
) {
    init {
        require(initialCount > 0)
    }

    private var count = initialCount
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()


    fun await() {
        lock.withLock {
            while (count > 0) {
                try {
                    condition.await()
                } catch (ex: InterruptedException) {
                    condition.signalAll()
                    throw ex
                }
            }
        }
    }

    fun await(timeout: Long, unit: TimeUnit): Boolean {
        lock.withLock {
            //fast path
            if (Thread.interrupted()) throw InterruptedException("Thread is interrupted") // caso da thread seja interrompida
            if (count == 0) return true

            // wait path
            var timeoutInNanos = unit.toNanos(timeout)
            while (true) {
                if (count > 0) {
                    try { // thread em espera ate ser sinalizada ou interrompida ou o tempo ter decorrido
                        timeoutInNanos = condition.awaitNanos(timeoutInNanos)
                    } catch (ex: InterruptedException) {
                        return false
                    }
                    // verificação das condições após o tempo ter decorrido
                    if (count == 0) return true
                    if (timeoutInNanos <= 0) return false
                }
            }
        }
    }

    fun countDown() = lock.withLock {
        if (count > 0) {
            count -= 1
            if (count == 0) {
                condition.signalAll()
            }
        }
    }

    fun getCount(): Long = lock.withLock {
        return count.toLong()
    }


}