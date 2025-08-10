package serie1
import org.junit.jupiter.api.Test
import serie1.countInRange
import java.util.*
import kotlin.test.assertEquals

class CountInRangeTest {

        @Test
        fun countInRange_onAnEmptyArray_returns0() {
            val v = IntArray(0)
            val res = countInRange(v, 0, 0, Int.MIN_VALUE, Int.MAX_VALUE)
            assertEquals(0, res)
        }

        @Test
        fun countInRange_withFullRange_returnsArrayLength() {
            val len = 100
            val v = IntArray(len)
            val r = Random()
            for (i in 0 until len) v[i] = r.nextInt()
            Arrays.sort(v)
            val res = countInRange(v, 0, v.size - 1, Int.MIN_VALUE, Int.MAX_VALUE)
            assertEquals(v.size, res)
        }

        @Test
        fun countInRange_withRepeatedValues_returnsCorrectedValue() {
            val v = intArrayOf(0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2)
            val res = countInRange(v, 0, v.size - 1, 1, 1)
            assertEquals(7, res)
        }

        @Test
        fun countInRange_withNoValuesInLimits() {
            val v = intArrayOf(0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 5, 5, 5, 6, 6, 6)
            val res = countInRange(v, 0, v.size - 1, 2, 3)
            assertEquals(0, res)
        }
    }

