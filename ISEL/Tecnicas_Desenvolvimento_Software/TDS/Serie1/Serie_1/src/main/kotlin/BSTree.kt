interface BSTree<T : Comparable<T>> {
    fun add(e: T): BSTree<T>
    operator fun contains(e: T): Boolean
    val height: Int
    fun forEachByOrder(visitor: (T) -> Unit)
}

class EmptyBSTree<T : Comparable<T>> : BSTree<T> {
    override fun add(e: T): BSTree<T> {
        return BSTreeImpl(e)
    }

    override fun contains(e: T): Boolean {
        return false
    }

    override val height: Int
        get() = 0

    override fun forEachByOrder(visitor: (T) -> Unit) {
    }
}

class BSTreeImpl<T : Comparable<T>>(
    private val value: T,
    private val left: BSTree<T>? = null,
    private val right: BSTree<T>? = null) : BSTree<T> {

    override fun add(e: T): BSTree<T> {
        return when {
            e < value -> BSTreeImpl(value, left?.add(e) ?: BSTreeImpl(e), right)
            e > value -> BSTreeImpl(value, left, right?.add(e) ?: BSTreeImpl(e))
            else -> this
        }
    }

    override fun contains(e: T): Boolean {
        return when {
            e < value -> left?.contains(e) ?: false
            e > value -> right?.contains(e) ?: false
            else -> true
        }
    }

    override val height: Int
        get() {
            val leftHeight = left?.height ?: 0
            val rightHeight = right?.height ?: 0
            return 1 + if (leftHeight > rightHeight) leftHeight else rightHeight
        }

    override fun forEachByOrder(visitor: (T) -> Unit) {
        left?.forEachByOrder(visitor)
        visitor(value)
        right?.forEachByOrder(visitor)
    }
}

fun <T: Comparable<T>> BSTree(): BSTree<T> = EmptyBSTree()
fun <T: Comparable<T>> bsTreeOf(vararg elems: T): BSTree<T> {
    var tree = BSTree<T>()
    for (elem in elems) {
        tree = tree.add(elem)
    }
    return tree
}
