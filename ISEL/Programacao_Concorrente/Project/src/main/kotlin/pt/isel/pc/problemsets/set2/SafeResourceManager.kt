package pt.isel.pc.problemsets.set2

import java.util.concurrent.atomic.AtomicInteger

class SafeResourceManager(
    private val obj: AutoCloseable,
    usages: Int
) {
    private var currentUsages = AtomicInteger(usages)

    fun release() {
        while (true) {
            val viewedUsages = currentUsages.get() // Usages observadas no momento
            if (viewedUsages == 0) {
                throw IllegalStateException("Usage count is already zero")
            }
            val newUsages = viewedUsages - 1
            if (currentUsages.compareAndSet(viewedUsages, newUsages)) { // verificação da da inalteração do valor anterior e
                                                                        // atualização do mesmo
                if (newUsages == 0) obj.close()
                return
            }
        }
    }
}