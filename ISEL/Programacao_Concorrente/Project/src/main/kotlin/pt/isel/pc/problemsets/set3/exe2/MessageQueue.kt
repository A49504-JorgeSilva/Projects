
package pt.isel.pc.problemsets.set3.exe2

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.LinkedList
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.coroutines.resume

class MessageQueue<T> {
    private val queue = LinkedList<T>()
    private val lock = ReentrantLock()
    private val requests = LinkedList<CancellableContinuation<T>>()

    fun enqueue(message: T) {
        lock.withLock {
            if (requests.isNotEmpty()) {
                val cont = requests.poll()
                cont.resume(message)
            } else {
                queue.addLast(message)
            }
        }
    }

    suspend fun dequeue(): T {
        return suspendCancellableCoroutine { cont ->
            lock.withLock {
                if (queue.isNotEmpty()) {
                    val message = queue.poll()
                    cont.invokeOnCancellation {
                        lock.withLock {
                            queue.addFirst(message)
                        }
                    }
                    cont.resume(message)
                } else {
                    requests.addLast(cont)
                    cont.invokeOnCancellation {
                        lock.withLock {
                            if (cont in requests) requests.remove(cont)
                        }
                    }
                }
            }
        }

    }
}