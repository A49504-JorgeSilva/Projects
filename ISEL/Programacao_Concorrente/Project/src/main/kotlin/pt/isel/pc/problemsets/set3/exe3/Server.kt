package pt.isel.pc.problemsets.set3.exe3

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.slf4j.LoggerFactory
import pt.isel.pc.PublishedMessage
import pt.isel.pc.RemoteClient
import pt.isel.pc.Subscriber
import pt.isel.pc.TopicName
import pt.isel.pc.TopicSet
import pt.isel.pc.problemsets.set3.exe1.acceptAsync
import pt.isel.pc.problemsets.set3.exe2.MessageQueue
import java.net.SocketAddress
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.random.Random

/**
 * The server component.
 */
class Server private constructor(
    private val serverSocket: AsynchronousServerSocketChannel,
    private val controlQueue: MessageQueue<ControlMessage>,
) {

    private val controlThread: Thread
    private val acceptThread: Thread
    private val clientSet = mutableSetOf<RemoteClient>()
    private val topicSet = TopicSet()
    private var currentClientId = 0
    private var state = State.RUNNING
    private var acceptThreadEnded = false

    init {
        controlThread = thread(isDaemon = true) {
            runBlocking {
                controlLoop()
            }
        }
        acceptThread = thread(isDaemon = true) {
            runBlocking {
                acceptLoop()
            }
        }
    }

    fun shutdown() {
        controlQueue.enqueue(ControlMessage.Shutdowm)
    }

    fun publish(message: PublishedMessage) {
        controlQueue.enqueue(ControlMessage.Publish(message))
    }

    fun subscribe(topicName: TopicName, subscriber: Subscriber) {
        controlQueue.enqueue(ControlMessage.Subscribe(topicName, subscriber))
    }

    fun unsubscribe(topicName: TopicName, subscriber: Subscriber) {
        controlQueue.enqueue(ControlMessage.Unsubscribe(topicName, subscriber))
    }

    fun remoteClientEnded(client: RemoteClient) {
        controlQueue.enqueue(ControlMessage.RemoteClientEnded(client))
    }

    fun join() {
        controlThread.join()
    }

    private fun handleNewClientSocket(clientSocket: AsynchronousSocketChannel) {
        if (state != State.RUNNING) {
            clientSocket.close()
            return
        }
        val newId = currentClientId++
        val remoteClient = RemoteClient.start(this, newId.toString(), clientSocket)
        clientSet.add(remoteClient)
        logger.info("Server: started new remote client")
    }

    private fun handleRemoteClientEnded(remoteClient: RemoteClient) {
        clientSet.remove(remoteClient)
        topicSet.unsubscribe(remoteClient)
        logger.info("Server: remote client ended {}", remoteClient.clientId)
        if (state == State.SHUTTING_DOWN) {
            if (clientSet.isEmpty() && acceptThreadEnded) {
                state = State.SHUTDOWN
            }
        }
    }

    private fun handlePublish(message: PublishedMessage) {
        topicSet.getSubscribersFor(message.topicName).forEach {
            it.send(message)
        }
    }

    private fun handleSubscribe(topicName: TopicName, subscriber: Subscriber) {
        topicSet.subscribe(topicName, subscriber)
    }

    private fun handleUnsubscribe(topicName: TopicName, subscriber: Subscriber) {
        topicSet.unsubscribe(topicName, subscriber)
    }

    private fun handleShutdown() {
        if (state != State.RUNNING) {
            return
        }
        startShutdown()
    }

    private fun startShutdown() {
        serverSocket.close()
        clientSet.forEach {
            it.shutdown()
        }
        state = State.SHUTTING_DOWN
    }

    private fun handleAcceptLoopEnded() {
        acceptThreadEnded = true
        if (state != State.SHUTTING_DOWN) {
            logger.info("Accept loop ended unexpectedly")
            startShutdown()
        }
        if (clientSet.isEmpty()) {
            state = State.SHUTDOWN
        }
    }

    private suspend fun controlLoop() {
        supervisorScope {
            launch(Dispatchers.Default) {
                try {
                    while (state != State.SHUTDOWN) {
                        try {
                            when (val controlMessage = controlQueue.dequeue()) {
                                is ControlMessage.NewClientSocket -> handleNewClientSocket(controlMessage.clientSocket)
                                is ControlMessage.RemoteClientEnded -> handleRemoteClientEnded(controlMessage.remoteClient)
                                is ControlMessage.Publish -> handlePublish(controlMessage.message)
                                is ControlMessage.Subscribe -> handleSubscribe(
                                    controlMessage.topicName,
                                    controlMessage.subscriber,
                                )

                                is ControlMessage.Unsubscribe -> handleUnsubscribe(
                                    controlMessage.topicName,
                                    controlMessage.subscriber,
                                )

                                ControlMessage.Shutdowm -> handleShutdown()
                                ControlMessage.AcceptLoopEnded -> handleAcceptLoopEnded()
                                else -> {}
                            }
                        } catch (ex: Throwable) {
                            logger.info("Unexpected exception, ignoring it", ex)
                        }
                    }
                } finally {
                    logger.info("server ending")
                }
            }
        }

    }

    private suspend fun acceptLoop() {
        supervisorScope {
            launch(Dispatchers.Default) {
                try {
                    while (true) {
                        val clientSocket = serverSocket.acceptAsync()
                        logger.info("New client socket accepted")
                        controlQueue.enqueue(ControlMessage.NewClientSocket(clientSocket))

                    }
                } catch (ex: Exception) {
                    logger.info("Exception on accept loop: {}", ex.message)
                    // continue
                } finally {
                    controlQueue.enqueue(ControlMessage.AcceptLoopEnded)
                }
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Server::class.java)
        fun start(address: SocketAddress): Server {
            val serverSocket = AsynchronousServerSocketChannel.open()
            serverSocket.bind(address)
            val controlQueue = MessageQueue<ControlMessage>()
            return Server(serverSocket, controlQueue)
        }
    }

    private sealed interface ControlMessage {
        data class NewClientSocket(val clientSocket: AsynchronousSocketChannel) : ControlMessage
        data class RemoteClientEnded(val remoteClient: RemoteClient) : ControlMessage
        data class Publish(val message: PublishedMessage) : ControlMessage
        data class Subscribe(val topicName: TopicName, val subscriber: Subscriber) : ControlMessage
        data class Unsubscribe(val topicName: TopicName, val subscriber: Subscriber) : ControlMessage

        data object Shutdowm : ControlMessage
        data object AcceptLoopEnded : ControlMessage
    }

    private enum class State {
        RUNNING,
        SHUTTING_DOWN,
        SHUTDOWN,
    }
}
