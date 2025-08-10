
package serie1

import kotlin.math.sign

/**
 * given an ordered array, [v], returns a new array, ordered, with each element squared.
 */
fun squaresSorted(v: IntArray): IntArray {
    var a = 0
    var b: Int
    var c = 0
    val newArray = IntArray(v.size)

    for (i in v.indices){
        if (v[i] < 0) {
            c++
        }
        v[i] = v[i] * v[i]
    }
    b = c - 1

    while (b >= 0 && c <= v.size - 1) {
        if (v[b] < v[c]){
            newArray[a] = v[b]
            b--
        }
        else {
            newArray[a] = v[c]
            c++
        }
        a++
    }

    while ( b >= 0){
        newArray[a] = v[b]
        b--
        a++
    }

    while (c <= v.size - 1){
        newArray[a] = v[c]
        c++
        a++
    }

    return newArray
}

/**
 * given a sub-array of ints, ordered, returns the number of elemens in which it's values are between min and max, including
 */
fun countInRange(v1: IntArray, l: Int, r: Int, min: Int, max: Int): Int {
    return if(v1.isEmpty()) 0
    else upperBound(v1, l, r, max) - lowerBound(v1, l, r, min) + 1
}

/**
 * Returns the number of sequences of numbers that summed equal [n]
 */
fun sumGivenN(n: Int): Int {
    var counter = 1
    var sum = 0

    //initializes the sequence of numbers, starting in 1 until n/2
    for (i in 1 .. n/2){
        var j = i

        //
        while (j <= (n/2) + 1){
            sum += j
            if (sum > n){
                sum = 0
                break
            }
            if (sum == n){
                counter++
                sum = 0
                break
            }
            j++
        }
    }

    return counter
}

/**
 *
 */
fun counter(array: IntArray, k: Int, lower: Int, upper: Int): Pair<Int, Int> {
    if (array.isEmpty() || array.size<k) return Pair(0,0)
    var lCount = 0
    var uCount = 0
    var left = 0
    var right = k
    while(right <=array.size){
        var sum =0
        var temp=left
        while (temp < right) {
            sum+=array[temp]
            temp++

        }

        if (sum == lower|| sum==upper) {
            left++
            right++
        }
        if (sum < lower) {
            lCount++
            left++
            right++
        }
        if (sum > upper) {
            uCount++
            left++
            right++
        }
    }
    return Pair(lCount, uCount)
}

/**
 * returns the number of times an inverted string is found in the array [v]
 */
fun countInverses(v: Array<String>, l: Int, r: Int): Int {
    var counter = 0

    for (i in l .. r){
        if (v[i].length == 1)
            counter++
        for (j in i + 1 ..  r){
            if (v[i] == v[j].reversed())
                counter++
        }
    }

    return counter
}


// Auxiliary methods
//......


fun upperBound(v: IntArray, l: Int, r: Int, max: Int): Int {
    var left = l
    var right = r
    var mid = 0
    while (left <= right) {
        mid = (right + left) / 2
        if (max >= v[mid]) left = mid + 1
        else right = mid - 1
    }
    return right
}

fun lowerBound(v: IntArray, l: Int, r: Int, min: Int): Int{
    var left = l
    var right = r
    var mid = 0
    while (left <= right){
        mid = (right + left)/2
        if (min <= v[mid]) right = mid - 1
        else left = mid + 1
    }
    return left
}
