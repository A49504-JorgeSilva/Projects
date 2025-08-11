package pt.isel.pc.problemsets.set3.exe1

import kotlinx.coroutines.runBlocking
import pt.isel.pc.problemsets.set3.exe1.acceptAsync
import java.net.InetSocketAddress
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import kotlin.test.Test
import kotlin.test.assertNotNull

class AsyncServerFunctionsTest {

    @Test
    fun `test AcceptSync`() = runBlocking {
        val server = AsynchronousServerSocketChannel.open()
        server.bind(InetSocketAddress(0)) // Bind to any available port

        val client = AsynchronousSocketChannel.open()
        client.connect(server.localAddress)

        val acceptedChannel = server.acceptAsync()

        assertNotNull(acceptedChannel)
        acceptedChannel.close()
        client.close()
        server.close()
    }
}