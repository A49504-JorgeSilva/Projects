package serie1

import org.junit.jupiter.api.Test
import serie1.countInverses
import kotlin.test.assertEquals

class CountInversesClass {
    @Test
    fun countInverses_OnAnEmptyArray() {
        val v = emptyArray<String>()
        val count = countInverses(v, 0, -1)
        assertEquals(0, count)
    }

    @Test
    fun countInverses_OnAnArrayWith1Element() {
        val v = arrayOf("aed")
        val count = countInverses(v, 0, 0)
        assertEquals(0, count)
    }

    @Test
    fun countInverses_OnAnArrayWithSomeElements() {
        val v = arrayOf("aed", "cad", "dae", "git", "otpx", "ptx", "xpto", "z")
        val count = countInverses(v, 0, v.size - 1)
        assertEquals(2, count)
    }

    @Test
    fun countInverses_OnAnArrayWithSomeUnOrderedElements() {
        val v = arrayOf("otpx", "cad", "dae", "z", "git", "ptx", "aed", "xpto")
        val count = countInverses(v, 0, v.size - 1)
        assertEquals(2, count)
    }

    @Test
    fun countInverses_OnAnArrayWithSomeOrderedElements1() {
        val v = arrayOf("abc", "cad", "cba", "ccba", "git", "ptx", "ztq")
        val count = countInverses(v, 0, v.size - 1)
        assertEquals(1, count)
    }

    @Test
    fun countInverses_OnAnArrayWithSomeOrderedElements2() {
        val v = arrayOf("abc", "acc", "caa", "caabbb", "cba", "git")
        val count = countInverses(v, 0, v.size - 1)
        assertEquals(1, count)
    }
}

