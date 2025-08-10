package serie1

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import serie1.squaresSorted

class SquareSortedClass {
    @Test
    fun squaresSorted_onArrayWithNoElements() {
        val array = intArrayOf()
        assertArrayEquals(array, squaresSorted(array))
    }

    @Test
    fun squaresSorted_onArrayWithOnePositiveElement() {
        val array = intArrayOf(3)
        assertArrayEquals(intArrayOf(9), squaresSorted(array))
    }

    @Test
    fun squaresSorted_onArrayWithOneNegativeElement() {
        val array = intArrayOf(-3)
        assertArrayEquals(intArrayOf(9), squaresSorted(array))
    }

    @Test
    fun squaresSorted_onArrayWithTwoElements1() {
        val array = intArrayOf(-3, 4)
        assertArrayEquals(intArrayOf(9, 16), squaresSorted(array))
    }

    @Test
    fun squaresSorted_onArrayWithTwoElements2() {
        val array = intArrayOf(-4, 3)
        assertArrayEquals(intArrayOf(9, 16), squaresSorted(array))
    }

    @Test
    fun squaresSorted_onArrayWithTwoElements3() {
        val array = intArrayOf(-3, 3)
        assertArrayEquals(intArrayOf(9, 9), squaresSorted(array))
    }

    @Test
    fun squaresSorted_onSomeArrayWithAGreaterPositiveElement() {
        val array = intArrayOf(-9, -9, -7, -3, -2, 0, 0, 1, 2, 2, 3, 4, 5, 6, 7, 7, 8, 10)
        assertArrayEquals(
            intArrayOf(0, 0, 1, 4, 4, 4, 9, 9, 16, 25, 36, 49, 49, 49, 64, 81, 81, 100),
            squaresSorted(array)
        )
    }

    @Test
    fun squaresSorted_onSomeArrayWithAGreaterNegativeElement() {
        val array = intArrayOf(-9, -9, -7, -3, -2, 0, 0, 1, 2, 2, 3, 4, 5, 6, 7, 7, 8)
        assertArrayEquals(intArrayOf(0, 0, 1, 4, 4, 4, 9, 9, 16, 25, 36, 49, 49, 49, 64, 81, 81), squaresSorted(array))
    }
}