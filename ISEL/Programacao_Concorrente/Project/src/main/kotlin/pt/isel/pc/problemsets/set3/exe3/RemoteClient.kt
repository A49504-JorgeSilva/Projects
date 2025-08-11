package pt.isel.pc

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import pt.isel.pc.problemsets.set3.exe1.readASync
import pt.isel.pc.problemsets.set3.exe1.writeASync
import pt.isel.pc.problemsets.set3.exe2.MessageQueue
import pt.isel.pc.problemsets.set3.exe3.Server
import pt.isel.pc.protocol.ClientRequest
import pt.isel.pc.protocol.ClientResponse
import pt.isel.pc.protocol.ServerPush
import pt.isel.pc.protocol.parseClientRequest
import pt.isel.pc.protocol.serialize
import pt.isel.pc.utils.SuccessOrError
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel


/**
 * The component responsible to interact with a remote client, via a [Socket].
 */
class RemoteClient private constructor(
    private val server: Server,
    val clientId: String,
    private val clientSocket: AsynchronousSocketChannel,
) : Subscriber {
    private val controlQueue = MessageQueue<ControlMessage>()
    private var state = State.RUNNING

    init {
        CoroutineScope(Dispatchers.Default).launch {
            logger.info("[{}] Remote client started control coroutine", clientId)
            controlLoop()
        }
        CoroutineScope(Dispatchers.Default).launch {
            logger.info("[{}] Remote client started read coroutine", clientId)
            readLoop()
        }
    }

    fun shutdown() {
        controlQueue.enqueue(ControlMessage.Shutdown)
    }

    override fun send(message: PublishedMessage) {
        controlQueue.enqueue(ControlMessage.Message(message))
    }

    private suspend fun handleShutdown(clientSocket: AsynchronousSocketChannel) {
        if (state != State.RUNNING) {
            return
        }
        val message = serialize(ServerPush.Bye) + "\n"
        val buffer = ByteBuffer.wrap(message.toByteArray())
        withContext(Dispatchers.IO) {
            clientSocket.writeASync(buffer)
            clientSocket.close()
        }
        state = State.SHUTTING_DOWN
    }

    private suspend fun handleMessage(clientSocket: AsynchronousSocketChannel, message: PublishedMessage) {
        if (state != State.RUNNING) {
            return
        }
        val msg = serialize(ServerPush.PublishedMessage(message)) + "\n"
        val buffer = ByteBuffer.wrap(msg.toByteArray())
        withContext(Dispatchers.IO) {
            clientSocket.writeASync(buffer)
        }
    }

    private suspend fun handleClientSocketLine(clientSocket: AsynchronousSocketChannel, line: String) {
        if (state != State.RUNNING) {
            return
        }
        val response = when (val res = parseClientRequest(line)) {
            is SuccessOrError.Success -> when (val request = res.value) {
                is ClientRequest.Publish -> {
                    server.publish(PublishedMessage(request.topic, request.message))
                    ClientResponse.OkPublish
                }
                is ClientRequest.Subscribe -> {
                    request.topics.forEach {
                        server.subscribe(it, this)
                    }
                    ClientResponse.OkSubscribe
                }
                is ClientRequest.Unsubscribe -> {
                    request.topics.forEach {
                        server.unsubscribe(it, this)
                    }
                    ClientResponse.OkUnsubscribe
                }
            }
            is SuccessOrError.Error -> {
                ClientResponse.Error(res.error)
            }
        }
        val message = serialize(response) + "\n"
        val buffer = ByteBuffer.wrap(message.toByteArray())
        withContext(Dispatchers.IO) {
            clientSocket.writeASync(buffer)
        }
    }

    private fun handleClientSocketError(throwable: Throwable) {
        logger.info("Client socket operation thrown: {}", throwable.message)
    }

    private fun handleClientSocketEnded() {
        if (state != State.RUNNING) {
            return
        }
        state = State.SHUTTING_DOWN
    }

    private fun handleReadLoopEnded() {
        state = State.SHUTDOWN
    }

    private suspend fun controlLoop() {
        try {
            val msg = serialize(ServerPush.Hi) + "\n"
            val buffer = ByteBuffer.wrap(msg.toByteArray())
            withContext(Dispatchers.IO) {
                clientSocket.writeASync(buffer)
            }
            while (state != State.SHUTDOWN) {
                val controlMessage = controlQueue.dequeue()
                logger.info("[{}] main thread received {}", clientId, controlMessage)
                when (controlMessage) {
                    ControlMessage.Shutdown -> handleShutdown(clientSocket)
                    is ControlMessage.Message -> handleMessage(clientSocket, controlMessage.value)
                    is ControlMessage.ClientSocketLine -> handleClientSocketLine(clientSocket, controlMessage.value)
                    ControlMessage.ClientSocketEnded -> handleClientSocketEnded()
                    is ControlMessage.ClientSocketError -> handleClientSocketError(controlMessage.throwable)
                    ControlMessage.ReadLoopEnded -> handleReadLoopEnded()
                    else -> {}
                }
            }

        } finally {
            logger.info("[{}] remote client ending", clientId)
            server.remoteClientEnded(this)
        }

    }

    private suspend fun readLoop() {
        val buffer = ByteBuffer.allocate(1024)
        val lineBuilder = StringBuilder()
        try {
            while (true) {
                buffer.clear()
                val bytesRead = withContext(Dispatchers.IO) {
                    clientSocket.readASync(buffer)
                }
                if (bytesRead == -1) {
                    logger.info("[{}] end of input stream reached", clientId)
                    controlQueue.enqueue(ControlMessage.ClientSocketEnded)
                    return
                }
                buffer.flip()
                val chars = Charsets.UTF_8.decode(buffer).toString()
                for (char in chars) {
                    if (char == '\n') {
                        // End of line reached, enqueue the line to the control queue
                        val line = lineBuilder.toString()
                        logger.info("[{}] line received: {}", clientId, line)
                        controlQueue.enqueue(ControlMessage.ClientSocketLine(line))
                        lineBuilder.clear()
                    } else {
                        // Add the character to the line builder
                        lineBuilder.append(char)
                    }
                }
            }
        } catch (ex: Throwable) {
            logger.info("[{}]Exception on read loop: {}, {}", clientId, ex.javaClass.name, ex.message)
            controlQueue.enqueue(ControlMessage.ClientSocketError(ex))
        } finally {
            logger.info("[{}] client loop ending", clientId)
            controlQueue.enqueue(ControlMessage.ReadLoopEnded)
        }
    }


    companion object {
        private val logger = LoggerFactory.getLogger(RemoteClient::class.java)
        fun start(server: Server, clientId: String, socket: AsynchronousSocketChannel): RemoteClient {
            return RemoteClient(
                server,
                clientId,
                socket,
            )
        }
    }

    private sealed interface ControlMessage {
        data class Message(val value: PublishedMessage) : ControlMessage
        data object Shutdown : ControlMessage
        data object ClientSocketEnded : ControlMessage
        data class ClientSocketError(val throwable: Throwable) : ControlMessage
        data class ClientSocketLine(val value: String) : ControlMessage
        data object ReadLoopEnded : ControlMessage
    }

    private enum class State {
        RUNNING,
        SHUTTING_DOWN,
        SHUTDOWN,
    }
}
