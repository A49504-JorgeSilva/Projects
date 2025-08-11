package pt.isel.pc.problemsets.set1

import pt.isel.pc.problemsets.set1.utils.NodeLinkedList
import java.time.Duration
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

/**
 * Monitor Style
 */
class Race<T> {
    val lock = ReentrantLock()
    val condition = lock.newCondition()
    val threadsQueue = NodeLinkedList<Thread>()
    var result: T? = null
    var isDone = false

    @Throws(InterruptedException::class)
    fun race(suppliers: List<() -> T>, timeout: Duration): T? {

        //fast-path
        suppliers.forEach { supplier ->
            val th = Thread.ofVirtual().start {
                try {
                    lock.withLock {
                        if (isDone) {
                            Thread.currentThread().interrupt()
                        } else {
                            val value = supplier()

                            if (!isDone) {
                                result = value
                                isDone = true
                                condition.signalAll()
                            }
                        }
                    }
                } catch (ie: InterruptedException) {
                    // do nothing
                }
            }

            threadsQueue.enqueue(th)
        }
        //wait path

        lock.withLock {
            var deadLine = timeout.toNanos()

            while (deadLine > 0 && result == null) {
                try {
                    deadLine = condition.awaitNanos(deadLine)
                } catch (ie: InterruptedException) {
                    threadsQueue.forEach { it.interrupt() }
                }

            }

        }

        threadsQueue.forEach { it.interrupt() }
        threadsQueue.forEach { it.join() }


        return result


    }


}

fun main() {
    val suppliers = listOf(
        { -> Thread.sleep(6000);1 },
        { -> Thread.sleep(6000);2 },
        { -> Thread.sleep(6000);3 },
        { -> Thread.sleep(6000);4 },
    )

    val res = Race<Int>().race(suppliers, Duration.ofSeconds(1))
    println("resultado $res")
}