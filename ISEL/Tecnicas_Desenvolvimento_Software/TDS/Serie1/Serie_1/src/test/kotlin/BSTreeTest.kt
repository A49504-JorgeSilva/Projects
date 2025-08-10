import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BSTreeTest {
    @Test
    fun `empty tree`() {
        val t = BSTree<Int>() // no root node
        assertFalse(2 in t)
        assertEquals(0, t.height)
    }

    @Test
    fun `one node tree`() {
        val t = BSTree<Char>().add('A') // t.root = Node(elem='A')
        assertTrue('A' in t)
        assertFalse('B' in t)
        assertEquals(1, t.height)
    }

    @Test
    fun `tree with 3 nodes`() {
        val elems = listOf(5, 3, 9)
        val t1 = elems.fold(BSTree<Int>()) { t, e -> t.add(e) }
        // t1.root = Node(elem=5, left=Node(3), right=Node(9))
        assertTrue(elems.all { it in t1 })
        assertEquals(2, t1.height)
    }

    @Test
    fun `Create a tree with multiple starting nodes`() {
        val t1 = bsTreeOf("ISEL", "TDS", "IPL", "Kotlin")
        // t1.root = Node(elem="TDS", left=Node("IPL"), right=Node("TDS", left=Node("Kotlin")))
        assertTrue(listOf("TDS", "ISEL", "IPL", "Kotlin").all { it in t1 })
        assertEquals(3, t1.height) // 3 nodes in the longest path
    }

    @Test
    fun `Nodes shared between trees`() {
        val t1 = bsTreeOf(5, 3, 9, 1)
        // t1.root = Node(elem=5, left=Node(3, left=Node(1) ), right=Node(9))
        val t2 = t1.add(10)
        // t2.root = Node(elem=5, left=t1.root.left, right=Node(9, right=Node(10))
        assertFalse(10 in t1)
        assertTrue(listOf(1, 3, 5, 9, 10).all { it in t2 })
        assertEquals(3, t1.height)
        assertEquals(3, t2.height)
    }

    @Test
    fun `visits each element through the infix path`() {
        val t = bsTreeOf(5, 3, 9, 1)
        var list = emptyList<Int>()
        t.forEachByOrder { list = list + it }
        assertEquals(listOf(1, 3, 5, 9), list)
    }
}
