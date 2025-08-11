package pt.isel.pc.problemsets.set1

import pt.isel.pc.problemsets.set1.utils.NodeLinkedList
import java.time.Duration
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Kernel Style
 */
class BlockingMessageQueue<T>(private val capacity : Int) {
    private val messageList = NodeLinkedList<T>()   //lista onde as mensagens vão ser armazenadas
    private val enqueueerList = NodeLinkedList<Enqueueer<T>>()
    private val consumerList = NodeLinkedList<Consumer<T>>()
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    data class Enqueueer<T>(
        val condition: Condition,
        val messages: List<T>
    )

    data class Consumer<T>(
        val condition: Condition,
        var item : T? = null,
    ){
        val isDone: Boolean
            get() = item != null
    }

    @Throws(InterruptedException::class)
    fun tryEnqueue(messages: List<T>, timeout: Duration): Boolean {
        lock.withLock {
            //fast path
            if (messageList.count + messages.size <= capacity){ //havendo espaço na fila, as msgs são adicionadas
                messages.forEach{
                    messageList.enqueue(it)
                }
                val head = consumerList.headNode ?: return true
                head.value.condition.signal()
                return true
            }

            //wait path
            var deadline = timeout.toNanos()
            val newEnqueuer = Enqueueer(lock.newCondition(), messages)
            val node = enqueueerList.enqueue(newEnqueuer)
            while (true) {
                try {
                    deadline = condition.awaitNanos(deadline)
                } catch (ie : InterruptedException) { //se a thread for interrompida é removida da lista de enqueuers
                    enqueueerList.remove(node)
                    throw ie
                }

                if (messageList.count + messages.size <= capacity) { //se houver espaço na fila, as msgs são adicionadas
                    enqueueerList.remove(node)
                    messages.forEach {
                        messageList.enqueue(it)
                    }
                    val head = consumerList.headNode ?: return true
                    head.value.condition.signal()
                    return true
                }

                if (deadline <= 0) {    //se o tempo de espera acabar, a thread é removida da lista de enqueuers
                    enqueueerList.remove(node)
                    return false
                }
            }
        }
    }

    @Throws(InterruptedException::class)
    fun tryDequeue(timeout: Duration): T? {
        lock.withLock {
            //fast path
            if (consumerList.notEmpty && messageList.notEmpty) { //se houver consumidores e mensagens na fila as mensagens são retiradas
                val message = messageList.pull().value
                val enqueueerHead = enqueueerList.headNode ?: return message
                enqueueerHead.value.condition.signal()
                return message
            }

            //wait path
            var deadline = timeout.toNanos()
            val node = consumerList.enqueue(Consumer(lock.newCondition()))
                while (true) {
                    try {   //se a thread for interrompida é removida da lista de consumidores
                        deadline = node.value.condition.awaitNanos(deadline)
                    } catch (ex: InterruptedException) {
                        consumerList.remove(node)
                        throw ex
                    }
                    if (messageList.notEmpty) { //se houver mensagens na fila, estas são retiradas
                        consumerList.remove(node)
                        val consumer = messageList.pull().value
                        val enqueueerHead = enqueueerList.headNode
                        val consumerHead = consumerList.headNode
                        return when {
                            consumerHead != null && enqueueerHead != null -> {  //se houver consumidores e enqueuers, estes são notificados
                                enqueueerHead.value.condition.signal()
                                consumerHead.value.condition.signal()
                                consumer
                            }
                            consumerHead == null && enqueueerHead != null -> {  //se houver enqueuers, estes são notificados
                                enqueueerHead.value.condition.signal()
                                consumer
                            }
                            consumerHead != null && enqueueerHead == null -> {  //se houver consumidores, estes são notificados
                                consumerHead.value.condition.signal()
                                consumer
                            }
                            else -> consumer    //se não houver consumidores nem enqueuers, a mensagem é retornada
                        }
                    }
                    if (deadline <= 0) {    //se o tempo de espera acabar, a thread é removida da lista de consumidores
                        consumerList.remove(node)
                        return null
                    }
                }
        }
        /*
        como funciona:
        Enqueue:
        -se a fila tem espaço, as msgs são adicionadas
        -se n há espaço , thread espera
        -quando uma msg é removida a prox thread é notificada

        dequeue:
        - se há msgs na fila, uma é removida
        - se estiver vazia, a thread espera
        - se uma msg é adicionada . a prox thread na lista é notificada
         */
    }
}