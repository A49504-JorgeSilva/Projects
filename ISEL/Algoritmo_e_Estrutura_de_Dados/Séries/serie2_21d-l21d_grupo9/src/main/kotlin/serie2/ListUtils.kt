package serie2

class Node<E> {
    var previous: Node<E>? = null
    var next: Node<E>? = null
    var value: E? = null
}

fun <E> intersection(list1: Node<E>, list2: Node<E>, cmp: Comparator<E>): Node<E>? {
    var l1 = list1.previous
    var l2 = list2.previous
    var head: Node<E>? = null


    while (l1 != list1 && l2 != list2) {
        if (cmp.compare(l1?.value, l2?.value) > 0) {
            if( l1?.value != head?.value) l1 = l1?.previous
            else {
                val aux1=l1
                //avancou
                l1 = l1?.previous
                //remover aux1 da lista 1
                l1?.next = aux1?.next
                aux1?.next?.previous = l1

            }

        }
        else if (cmp.compare(l1?.value, l2?.value) < 0) {
            if( l2 != head) l2 = l2?.previous
            else{
                val aux2=l1
                //avancou
                l2 = l2?.previous
                //remover aux1 da lista 1
                l2?.next = aux2?.next
                aux2?.next?.previous = l2
            }

        }
        else {
            val aux1=l1
            val aux2=l2
            //avancou
            l1 = l1?.previous
            l2 = l2?.previous
            //remover aux1 da lista 1
            l1?.next = aux1?.next
            aux1?.next?.previous = l1

            //remover aux2 da lista 2
            l2?.next = aux2?.next
            aux2?.next?.previous = l2
            //add
            if(aux1?.value!=head?.value) {
                aux1?.next = head
                if (head != null) {
                    // adc ao aux1 a cabeca da lista head
                    head.previous = aux1
                }
                head = aux1
            }

        }

    }
    return head
}

fun <E> getKBiggest(list: Node<E>?, k: Int): E? {
    var value = list
    if (k <= 0) return null
    var head = list
    var listSize = 0
    while (head != null){
        listSize ++
        head = head.next
    }
    println("listSize = " + listSize)
    if (listSize < k)return null

    repeat(listSize - k ){value = value?.next}
    return value?.value
}

fun <E> quicksort(first: Node<E>?, last: Node<E>?, cmp: Comparator<E>) {
    return _quickSort(first, last, cmp)
}

fun <E> partition (first: Node<E>? , last: Node<E>? , cmp: Comparator<E>): Node<E>? {
    var x = last?.value
    var i = first?.previous
    var j = first


    while (j != last ) {
        if (cmp.compare(j?.value, x) <= 0) {
            if (i == null) i = first else i =  i.next
            val  temp = i?.value
            i?.value = j?.value
            j?.value = temp
        }
        j = j?.next
    }
    if (i == null) i = first else i = i.next
    val  temp = i?.value
    i?.value = last?.value
    last?.value = temp

    return i
}

fun <E> _quickSort (first: Node<E>? , last: Node<E>? , cmp: Comparator<E>) {
    var temp: Node<E>

    if (last != null && first != last && first != last.next) {
        temp = partition(first, last, cmp)!!
        _quickSort(first, temp.previous, cmp)
        _quickSort(temp.next, last, cmp)
    }
}