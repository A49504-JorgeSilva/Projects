package serie2

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Get_K_BiggestTest {
    var aList = mutableListOf(2, 4, 6, 8, 10, 12, 14, 18, 20)

    @Test
    fun getKBiggest_emptyList_kSmallerThanOne() {
        val list = emptyListWithoutSentinel<Int>()
        Assertions.assertEquals(null, getKBiggest(list, 0))
        Assertions.assertEquals(null, getKBiggest(list, -1))
    }

    @Test
    fun getKBiggest_emptyList_kGreaterThanListLength() {
        val list = emptyListWithoutSentinel<Int>()
        Assertions.assertEquals(null, getKBiggest(list, 9))
    }

    @Test
    fun getKBiggest_emptyList_kSmallerThanListLength() {
        val list = emptyListWithoutSentinel<Int>()
        Assertions.assertEquals(null, getKBiggest(list, 3))
    }

    @Test
    fun getKBiggest_kSmallerThanOne() {
        val list = getListWithoutSentinel(aList)
        Assertions.assertEquals(null, getKBiggest(list, 0))
        Assertions.assertEquals(null, getKBiggest(list, -1))
    }

    @Test
    fun getKBiggest_kEqualsListLength() {
        val list = getListWithoutSentinel(aList)
        Assertions.assertEquals(2, getKBiggest(list, 9))
    }

    @Test
    fun getKBiggest_kGreaterThanListLength() {
        val list = getListWithoutSentinel(aList)
        Assertions.assertEquals(null, getKBiggest(list, 10))
    }

    @Test
    fun getKBiggest_kSmaller_ThanListLength() {
        val list = getListWithoutSentinel(aList)
        Assertions.assertEquals(14, getKBiggest(list, 3))
    }
}