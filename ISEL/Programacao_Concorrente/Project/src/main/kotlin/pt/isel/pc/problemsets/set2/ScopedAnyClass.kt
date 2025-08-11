package pt.isel.pc.problemsets.set2

import java.util.concurrent.CompletableFuture
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ScopedAnyClass<T>(
    private val lock: ReentrantLock = ReentrantLock()
) {
    class AllFutureFailedClass(msg: String) : RuntimeException(msg)
    fun <T> scopedAny(
        futures: List<CompletableFuture<T>>,
        onSuccess: (T) -> Unit
    ): CompletableFuture<T> {
        require(futures.isNotEmpty())
        var success: T? = null
        val failed = AllFutureFailedClass("All futures failed")
        val res = CompletableFuture<T>()
        futures.forEach { future ->
            future.handle { value, throwable ->
                lock.withLock {
                    if (throwable != null) {
                        failed.addSuppressed(throwable)
                    } else {
                        if (success == null) { //no caso de ser o primeiro sucesso realizamos a chamada ao metodo onSuccess
                            success = value    // e atualizamos o valor de success
                            onSuccess(value)
                        }
                    }
                    when{
                        success == null && futures.all { it.isDone } -> res.completeExceptionally(failed) //no caso de todos os futures se completarem e nenhum ter sucesso
                        success != null && futures.all { it.isDone }  -> res.complete(success)// no caso de todos os futures se completarem e algum ter sucesso
                        else -> {}
                    }
                }
            }
        }
        return res
    }
}