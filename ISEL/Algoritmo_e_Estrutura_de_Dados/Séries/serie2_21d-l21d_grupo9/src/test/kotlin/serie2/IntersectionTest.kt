package serie2

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class IntersectionTest {
    val CMP_NATURAL_ORDER:(Int,Int)->Int = { i1, i2 -> i1.compareTo(i2) }



    @Test
    fun intersection_empty_lists() {
        var list1: Node<Int> = emptyListWithSentinel()
        var list2: Node<Int> = emptyListWithSentinel()
        assertTrue(isEmptyListWithoutSentinel(intersection(list1, list2, CMP_NATURAL_ORDER)))
        list1 = getList(0, 1, 1, null)
        list2 = emptyListWithSentinel()
        assertTrue(isEmptyListWithoutSentinel(intersection(list1, list2, CMP_NATURAL_ORDER)))
        list1 = emptyListWithSentinel()
        list2 = getList(0, 1, 1, null)
        assertTrue(isEmptyListWithoutSentinel(intersection(list1, list2, CMP_NATURAL_ORDER)))
    }

    @Test
    fun intersection_singleton_lists() {
        var list1: Node<Int> = getList(0, 1, 1, null)
        var list2: Node<Int> = getList(1, 1, 1, null)
        assertTrue(isEmptyListWithoutSentinel(intersection(list1, list2, CMP_NATURAL_ORDER)))
        assertFalse(isEmptyListWithSentinel(list1))
        assertFalse(isEmptyListWithSentinel(list2))
        list1 = getList(1, 1, 1, null)
        list2 = getList(1, 1, 1, null)
        val res: Node<Int>? = intersection(list1, list2, CMP_NATURAL_ORDER)
        assertEquals(1, res!!.value)
        assertTrue(isEmptyListWithSentinel(list1))
        assertTrue(isEmptyListWithSentinel(list2))
    }

    @Test
    fun intersection_different_lists() {
        val list1: Node<Int> = getList(0, 10, 2, null)
        val list2: Node<Int> = getList(1, 10, 2, null)
        assertTrue(isEmptyListWithoutSentinel(intersection(list1, list2, CMP_NATURAL_ORDER)))
        assertFalse(isEmptyListWithSentinel(list1))
        assertFalse(isEmptyListWithSentinel(list2))
    }

    @Test
    fun intersection_equal_lists() {
        val elements = ArrayList<Int>()
        val list1: Node<Int> = getList(0, 10, 1, elements)
        val list2: Node<Int> = getList(0, 10, 1, null)
        val res: Node<Int>? = intersection(list1, list2, CMP_NATURAL_ORDER)
        var curr: Node<Int>? = res
        for (i in 0 until elements.size) {
            curr?.let{assertTrue(elements[i].equals(it.value))}
            curr = curr?.let{it.next}
        }
        assertTrue(isEmptyListWithSentinel(list1))
        assertTrue(isEmptyListWithSentinel(list2))
    }

    @Test
    fun intersection_mix_lists() {
        val elements = ArrayList<Int>()
        val list1: Node<Int> = getList(1, 10, 2, null)
        val list2: Node<Int> =getList(1, 10, 4, elements)
        val res: Node<Int>? = intersection(list1, list2, CMP_NATURAL_ORDER)
        var curr: Node<Int>? = res
        for (i in 0 until elements.size) {
            curr?.let{assertTrue(elements[i].equals(it.value))}
            curr = curr?.let{it.next}
        }
        assertFalse(isEmptyListWithSentinel(list1))
        assertTrue(isEmptyListWithSentinel(list2))
    }

    @Test
    fun intersection_with_duplicates_lists() {
        val elements = ArrayList<Int>()
        val elementsDups = ArrayList<Int>()
        val list1: Node<Int> = getListDups(1, 10, 2, null)
        val list2: Node<Int> = getListDups(1, 10, 4, elementsDups)
        val res: Node<Int>? = intersection(list1, list2, CMP_NATURAL_ORDER)
        var curr: Node<Int>? = res
        var prevValue: Int? = null
        for (i in 0 until elementsDups.size) {
            if (elementsDups[i] !== prevValue) {
                elements.add(elementsDups[i])
                prevValue = elementsDups[i]
            }
        }
        for (i in 0 until elements.size) {
            curr?.let{assertTrue(elements[i].equals(it.value))}
            curr = curr?.let{it.next}
        }
        assertFalse(isEmptyListWithSentinel(list1))
        assertTrue(isEmptyListWithSentinel(list2))
    }
}