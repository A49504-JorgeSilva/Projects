package pt.isel.pc.problemsets.set2

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReferenceArray

class SafeSuccession<T>(
    private val items: Array<T>
) {
    private var index = AtomicInteger(0)

    fun next(): T? {
        while (true) {
            val currentIndex = index.get()  //obter o valor de index atual
            if (currentIndex < items.size) {
                val observedValue = items[currentIndex] // item correspondente ao index atual
                val nextIndex = currentIndex + 1
                if (index.compareAndSet(currentIndex, nextIndex)) return observedValue //verificação da da inalteração do valor anterior e atualização do mesmo
            } else {
                return null
            }
        }
    }
}