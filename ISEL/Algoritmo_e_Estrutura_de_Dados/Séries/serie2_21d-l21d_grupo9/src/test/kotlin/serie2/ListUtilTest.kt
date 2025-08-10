package serie2
import java.util.*


/*
* Double Linked Lists Without Sentinel, Non_circular
* */
fun <E> emptyListWithoutSentinel(): Node<E>? {
    return null
}

fun getListWithoutSentinel(begin: Int, end: Int, step: Int): Node<Int>? {
    var begin = begin
    if (end < begin) return null
    val list = Node<Int>()
    var cur: Node<Int>? = list
    if(cur!=null) cur.value = begin
    begin += step
    while (begin < end) {
        val next = Node<Int>()
        if(cur!=null) cur.next = next
        next.previous = cur
        next.value = begin
        if(cur!=null) cur = cur.next
        begin += step
    }
    return list
}

fun <E> isEmptyListWithoutSentinel(list: Node<E>?): Boolean {
    return list == null
}

fun <E> isSorted(list: Node<E>?, last: Node<E>?, cmp: Comparator<E>): Boolean {
    var curr = list
    if (curr == null || curr == last) return true
    while (curr?.let{it.next} != last) {
        if (cmp.compare(curr?.let{it.value}, curr?.let{it.next?.let{it.value}}) > 0) return false
        curr = curr?.let{it.next}
    }
    return true
}


/*
  * For circular double linked lists with sentinel
  */
fun <E> emptyListWithSentinel(): Node<E> {
    val empty = Node<E>()
    empty.previous = empty
    empty.next = empty.previous
    return empty
}

fun <E> isEmptyListWithSentinel(list: Node<E>): Boolean {
    return list.next == list && list.previous == list
}



fun getList(start: Int, length: Int, step: Int, array: MutableList<Int>?): Node<Int> {
    val list: Node<Int> = emptyListWithSentinel()
    var i = length - 1
    while (i >= 0) {
        list.next = newNode(i + start, list, list.next)
        list.next?.let {
            it.next?.let { it1 -> it1.previous = list.next }}
            i -= step
        }
        if (array != null) {
            var current = list.next
            while (current != list && current!=null) {
                current.value?.let { array.add(it) }
                current = current.next
            }
        }
        return list
    }

    fun getListDups(start: Int, length: Int, step: Int, array: MutableList<Int>?): Node<Int> {
        val list: Node<Int> = emptyListWithSentinel()
        var count = 1
        var i = length - 1
        while (i >= 0) {
            for (k in 0 until count) {
                list.next = newNode(i + start, list, list.next)
                list.next?.let { it.next?.let { it.previous = list.next } }
            }
            count++
            i -= step
        }
        if (array != null) {
            var current = list.next
            while (current != list && current!=null) {
                current.value?.let { array.add(it) }
                current = current?.let{it.next}
            }
        }
        return list
    }


    fun getRandomList(dimension: Int): Node<Int>? {
        val r = Random()
        var list: Node<Int>? = emptyListWithoutSentinel()
        for (i in 0 until dimension) {
            val novo = newNode(r.nextInt() % 40)
            novo.next = list
            if (list != null) list.previous = novo
            list = novo
        }
        return list
    }

    fun getListWithoutSentinel(array: MutableList<Int>): Node<Int>? {
        if (array.size == 0) return null
        else {
            val list = Node<Int>()
            var cur: Node<Int>? = list
            cur?.let { it.value = array[0] }
            for (i in 1 until array.size) {
                val next = Node<Int>()
                cur?.let { it.next = next }
                next.previous = cur
                next.value = array[i]
                if(cur!=null)cur = cur.next
            }
            return list
        }
    }


    /*
* Single Linked List
* */

    fun <E> getNNode(N: Int, list: Node<E>?): Node<E>? {
        var list = list
        for (i in 0 until N) {
            if (list == null) return null
            list = list.next
        }
        return list
    }


    /*
 *
 * Generic Methods
 */
    fun <E> newNode(v: E): Node<E> {
        val result = Node<E>()
        result.value = v
        return result
    }

    fun <E> newNode(v: E, p: Node<E>, n: Node<E>?): Node<E> {
        val result = newNode(v)
        result.previous = p
        result.next = n
        return result
    }


