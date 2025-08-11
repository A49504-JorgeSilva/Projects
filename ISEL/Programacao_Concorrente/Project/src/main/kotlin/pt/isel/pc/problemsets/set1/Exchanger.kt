package pt.isel.pc.problemsets.set1

import pt.isel.pc.problemsets.set1.utils.NodeLinkedList
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class Exchanger<T> {

    private val lock = ReentrantLock()
    private val exchangeList = NodeLinkedList<ThreadMock<T>>()
    data class ThreadMock<T>(val condition: Condition, var item: T, var isDone: Boolean = false)

    fun exchange(obj: T): T {
        lock.withLock {
            //fast path
            val head = exchangeList.headNode
            if (head != null) {
                val value = head.value.item
                head.value.item = obj
                exchangeList.remove(head)
                head.value.isDone = true
                head.value.condition.signal()
                return value
            }

            //wait path
            val newObj = ThreadMock(lock.newCondition(), obj)
            exchangeList.enqueue(newObj)
            newObj.condition.await()

            return newObj.item


        }

    }

    fun exchange(obj: T, timeout: Long, unit: TimeUnit): T {
        lock.withLock {
            //fast path
            val head = exchangeList.headNode
            if (head != null) {
                val value = head.value.item
                head.value.item = obj
                exchangeList.remove(head)
                head.value.isDone = true
                head.value.condition.signal()
                return value
            }

            //wait path
            if (timeout <= 0) throw TimeoutException()
            var deadline = unit.toNanos(timeout)
            val newObj = ThreadMock(lock.newCondition(), obj)
            val newNode = exchangeList.enqueue(newObj)

            while (true) {
                try {
                    deadline = newNode.value.condition.awaitNanos(deadline)
                } catch (ex: InterruptedException) {
                    if (newNode.value.isDone){
                        Thread.currentThread().interrupt()
                        return newNode.value.item
                    }
                    exchangeList.remove(newNode)
                    throw ex
                }

                if (newNode.value.isDone) return newNode.value.item

                if (deadline <= 0) {
                    exchangeList.remove(newNode)
                    throw TimeoutException()
                }
            }
        }
    }
}