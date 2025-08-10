data class Node<E>(var item: E, var left: Node<E>?, var right: Node<E>?)

fun <E> isComplete(root: Node<E>?): Boolean {
    if (root == null) return true
    val height = getHeight(root)
    return isComplete(root, 0, height)
}

fun <E> getHeight(node: Node<E>?): Int {
    if (node == null) return -1
    val leftHeight = getHeight(node.left)
    val rightHeight = getHeight(node.right)
    return 1 + if (leftHeight > rightHeight) leftHeight else rightHeight
}

fun <E> isComplete(node: Node<E>?, level: Int, height: Int): Boolean {
    if (node == null) return true
    if (level == height) return node.left == null || node.right == null
    if (node.left == null || node.right == null) return false
    return isComplete(node.left, level + 1, height) && isComplete(node.right, level + 1, height)
}


fun <E> isBST(root: Node<E>?, cmp: Comparator<E>): Boolean {
    return isBST(root, null, null, cmp)
}

fun <E> isBST(node: Node<E>?, min: E?, max: E?, cmp: Comparator<E>): Boolean {
    if (node == null) return true
    if (min != null && cmp.compare(node.item, min) <= 0) return false
    if (max != null && cmp.compare(node.item, max) >= 0) return false
    return isBST(node.left, min, node.item, cmp) && isBST(node.right, node.item, max, cmp)
}

fun countMultiples(root: Node<Int>?, k: Int): Int {
    if (root == null) return 0
    var count = 0
    if (root.item % k == 0) count++
    count += countMultiples(root.left, k)
    count += countMultiples(root.right, k)
    return count
}

