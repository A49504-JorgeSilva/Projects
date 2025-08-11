package pt.isel.pc.problemsets.set1

import org.slf4j.LoggerFactory
import pt.isel.pc.problemsets.set1.utils.NodeLinkedList
import java.time.Duration
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Kernel Style
 */
class ThreadPoolExecutor(
    private val maxThreadPoolSize: Int,
    private val keepAliveTime: Duration
) {
    private val lock = ReentrantLock()
    private val workAvailable = lock.newCondition()
    private var nOfThreads: Int = 0
    private var waitingThreads = 0
    private var workItems = NodeLinkedList<Runnable>()
    private var isShutting = false

    val shuttingFlag: Boolean
        get() = lock.withLock { isShutting }
    val threadsCount: Int
        get() = lock.withLock { nOfThreads }

    val waitingCount: Int
        get() = lock.withLock { waitingThreads }


    init {
        require(maxThreadPoolSize > 0) { "maxThreads must be greater than zero" }
    }

    @Throws(RejectedExecutionException::class)
    fun execute(runnable: Runnable) {
        lock.withLock {
            when {
                isShutting -> throw RejectedExecutionException() // no caso da flag de shutting down estiver ativa não se executa mais runnables
                waitingCount > 0 -> { // se houver threads em espera estas devem ser sinalizadas para o novo trabalho que "chegou"
                    workItems.enqueue(runnable)
                    workAvailable.signal()
                }

                threadsCount < maxThreadPoolSize -> { // no caso de não haver threads em espera e o numero de threads ativas for menor que o maxThreadPoolSize,
                                                    // criamos uma nova thread para o runnable passado como parametro
                    nOfThreads += 1
                    Thread.ofPlatform().start {
                        workerThreadLoop(runnable)
                    }
                }

                else -> workItems.enqueue(runnable) // em ultimo caso, apenas colocamos o runnable na lista de items em espera
            }
        }

    }

    fun shutdown() {
        lock.withLock {
            if (!isShutting) {
                isShutting = true
            }
        }
    }

    @Throws(InterruptedException::class)
    fun awaitTermination(timeout: Duration): Boolean {
        lock.withLock {
            //fast path
            if (workItems.empty && threadsCount == 0) return true // se não houver mais threads ativas e não houver mais runnables

            //wait path
            var deadLine = timeout.toNanos()
            while (true) {
                try {
                    deadLine = workAvailable.awaitNanos(deadLine) // a thread fica em espera o tempo determinado
                } catch (ex: InterruptedException) { // em caso de interrupção da thread verificamos se os casos otimistas foram alcançados
                    if (workItems.empty && threadsCount == 0) return true
                    if (deadLine <= 0) return false
                    throw ex
                }
                // após o tempo determinado ter decorrido, verifica-se as condições pretendidas
                if (workItems.empty && threadsCount == 0) return true

                if (deadLine <= 0) return false
            }
        }
    }

    private sealed interface GetNextWorkItemResult {
        data class WorkItem(val item: Runnable) : GetNextWorkItemResult
        object Exit : GetNextWorkItemResult
    }

    private fun getNextRunnable(): GetNextWorkItemResult {
        lock.withLock {
            var deadline = keepAliveTime.toNanos()
            while (true) {
                if (isShutting) { // no caso da flag estar ativa terminamos a thread
                    nOfThreads -= 1
                    return GetNextWorkItemResult.Exit
                }

                if (workItems.notEmpty) { // no caso de haver runnables, vai-se buscar o runnable e a thread trabalha esse runnable
                    return GetNextWorkItemResult.WorkItem(workItems.pull().value)
                }

                try {   // no caso das duas condições anteriores não se verificarem, a thread fica em espera o tempo determinado por KeepAliveTime
                        // até que esta seja sinalizada ou o tempo seja ultrapassado
                    waitingThreads += 1
                    deadline = workAvailable.awaitNanos(deadline)
                } catch (ex: InterruptedException) { // em caso de interrupção da thread esta é terminada
                    waitingThreads -= 1
                    nOfThreads -= 1
                    throw ex
                }
                // após o tempo de espera ter decorrido verifica-se as condições pretendidas
                if (workItems.notEmpty) {
                    waitingThreads -= 1
                    return GetNextWorkItemResult.WorkItem(workItems.pull().value)
                }

                if (deadline <= 0) {
                    waitingThreads -= 1
                    nOfThreads -= 1
                    return GetNextWorkItemResult.Exit
                }


            }
        }
    }

    private fun workerThreadLoop(initialWorkItem: Runnable) {
        var workItem = initialWorkItem
        while (true) {
            safeRun(workItem)
            workItem = when (val res = getNextRunnable()) {
                is GetNextWorkItemResult.WorkItem -> res.item
                GetNextWorkItemResult.Exit -> return
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ThreadPoolExecutor::class.java)
        private fun safeRun(runnable: Runnable) {
            try {
                runnable.run()
            } catch (ex: Throwable) {
                logger.warn("Unexpected exception, ignoring it to keeping worker thread alive")
                // ignore
            }
        }
    }
}