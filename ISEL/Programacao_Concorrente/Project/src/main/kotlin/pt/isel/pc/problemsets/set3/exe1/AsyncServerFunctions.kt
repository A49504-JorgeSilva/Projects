package pt.isel.pc.problemsets.set3.exe1

import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun AsynchronousServerSocketChannel.acceptAsync(): AsynchronousSocketChannel {
    return suspendCancellableCoroutine { cont ->
        val handler =  object: CompletionHandler<AsynchronousSocketChannel, Any?>{
            //o socket perde-se e cancelar a operação
            override fun completed(result: AsynchronousSocketChannel, attachment: Any?) {  //o socket perde-se e cancelar a operação
                if (!cont.isCancelled)
                    cont.resume(result) // todo: função adicional caso nao seja entregue result
                else
                    result.close()
            }
            override fun failed(exc: Throwable, attachment: Any?) {
                cont.resumeWithException(exc)

            }
        }
        accept(null,handler)

        cont.invokeOnCancellation {
            try {
                close()
            } catch (ignore: Throwable) {
            }
        }
    }
}

suspend fun AsynchronousSocketChannel.readASync(buffer: ByteBuffer): Int {
    return suspendCancellableCoroutine { cont ->
        read(buffer, null, object : CompletionHandler<Int, Any?> {
            override fun completed(result: Int, attachment: Any?) {
                cont.resume(result)
            }

            override fun failed(exc: Throwable, attachment: Any?) {
                cont.resumeWithException(exc)
            }
        })
    }
}

suspend fun AsynchronousSocketChannel.writeASync(buffer: ByteBuffer): Int {
    return suspendCancellableCoroutine { cont ->
        write(buffer, null, object : CompletionHandler<Int, Any?> {
            override fun completed(result: Int, attachment: Any?) {
                cont.resume(result)
            }
            override fun failed(exc: Throwable, attachment: Any?) {
                cont.resumeWithException(exc)
            }
        })
    }
}