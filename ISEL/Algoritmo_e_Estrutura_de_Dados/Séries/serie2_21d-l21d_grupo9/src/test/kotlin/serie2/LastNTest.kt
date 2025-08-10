package serie2

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LastNTest {


    @Test
    fun LastN_singleton() {
        val ar=LastN(1)
        ar.offer(5)
        assertEquals(5,ar.get(0))
        assertEquals(5,ar.sum())
        ar.offer(10)
        assertEquals(10,ar.get(0))
        assertEquals(10,ar.sum())
    }

    @Test
    fun LastN_someElements() {
        val ar=LastN(5)
        for(i in 1..5)
            ar.offer(i)
        for(i in 1..5)
         assertEquals(i,ar.get(i-1))
        assertEquals(15,ar.sum())
        ar.offer(6)
        for(i in 2..5)
         assertEquals(i,ar.get(i-1))
        assertEquals(20,ar.sum())
    }
}