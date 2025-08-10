package serie2

class LastN {

    constructor(n:Int){
        array= IntArray(n)
        tail=-1
    }

    private var array: IntArray
    private var head: Int = 0
    private var summation:Int=0
    private var tail:Int
    private var capacity=0

    fun offer (x:Int): Boolean {
        tail++
        if(tail == array.size)
            tail %= array.size
        if (capacity == array.size){
            head = head++ % array.size
            capacity--
        }
        summation = summation - array[tail] + x
        array[tail] = x
        capacity++
        return true
    }

    fun get(i:Int):Int?{
        if(array.isNotEmpty()){
            return if (head+i > array.size) array[(head+i)%array.size]
            else array[head+i]
        }
        return null
    }

    fun sum():Int{
        return summation
    }
}
