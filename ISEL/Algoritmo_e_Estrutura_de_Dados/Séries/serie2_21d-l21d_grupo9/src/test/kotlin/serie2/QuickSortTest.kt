package serie2

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


    class QuickSortTest {


        val CMP_REVERSE_ORDER: (Int, Int) -> Int = { i1, i2 -> i2.compareTo(i1) }
        val CMP_NATURAL_ORDER: (Int, Int) -> Int = { i1, i2 -> i1.compareTo(i2) }

        @Test
        fun quicksort_empty_list() {
            val list: Node<Int>? = emptyListWithoutSentinel()
            quicksort(list, list, CMP_NATURAL_ORDER)
            assertTrue(isSorted(list, list, CMP_NATURAL_ORDER))
        }

        @Test
        fun quicksort_with_oneElement_list() {
            val list: Node<Int>? = getRandomList(1)
            list?.let { quicksort(list, it.next, CMP_NATURAL_ORDER) }
            list?.let { assertTrue(isSorted(list, it.next, CMP_NATURAL_ORDER)) }
        }

        @Test
        fun quicksort_with_increasingElements() {
            val list: Node<Int>? = getListWithoutSentinel(0, 20, 1)
            for (i in 0..19) {
                val last: Node<Int>? = getNNode(i, list)
                quicksort(list, last, CMP_REVERSE_ORDER)
                assertTrue(isSorted(list, last, CMP_REVERSE_ORDER))
            }
        }

        @Test
        fun quicksort_with_SomeElements1_list() {
            val array = mutableListOf(22, -30, -30, 32, -38, -34, -36)
            val list: Node<Int>? = getListWithoutSentinel(array)
            val last: Node<Int>? = getNNode(6, list)
            quicksort(list, last, CMP_NATURAL_ORDER)
            assertTrue(isSorted(list, last, CMP_NATURAL_ORDER))
        }

        @Test
        fun quicksort_with_som2Elements2_list() {
            val array = mutableListOf(27, -12, 33, 27, 26, -33, 14, 26, 8, 9, -19)
            val list: Node<Int>? = getListWithoutSentinel(array)
            val last: Node<Int>? = getNNode(10, list)
            quicksort(list, last, CMP_NATURAL_ORDER)
            assertTrue(isSorted(list, last, CMP_NATURAL_ORDER))
        }

        @Test
        fun quicksort_with_randomElements_list() {
            val list: Node<Int>? = getRandomList(11)
            val last: Node<Int>? = getNNode(10, list)
            quicksort(list, last, CMP_NATURAL_ORDER)
            assertTrue(isSorted(list, last, CMP_NATURAL_ORDER))
        }


    }

