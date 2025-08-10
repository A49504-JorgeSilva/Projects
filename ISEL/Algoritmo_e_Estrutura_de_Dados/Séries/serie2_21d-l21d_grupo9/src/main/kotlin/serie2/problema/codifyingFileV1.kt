package serie2.problema

import serie2.createWriter
import serie2.readFileWords
import java.util.*
import kotlin.collections.HashMap
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

fun main(args: Array<String>){
    codifyingFileV1(args)
}

fun codifyingFileV1(args: Array<String>){

    if (args.size < 2)
        throw Exception("There has to be 2 arguments")

    val outputFile = createWriter(args[1])

    val wordMap = HashMap<String, Int>()

    //reads the file and adds the words to the hashMap with the corresponding value (number of times each word occurs)
    readFileWords(args[0]).forEach { word ->
        wordMap[word] = (wordMap.getOrPut(word) { 0 } as Int) + 1
    }

    //make a new hashMap that is sorted first by its key and then by its values
    val mapToList = wordMap.toList()
    val sortedMapByKey = mapToList.sortedBy { it.first }.reversed()
    val fullySortedMap = sortedMapByKey.sortedBy { it.second }.toMap()

    //get the array of the sorted keys
    val listOfKeys = fullySortedMap.keys.toList()

    //get the codedMap. Each word (key) will now have its corresponding word (value)
    val codedMap = HashMap<String, String>()
    var i = 0
    var j = listOfKeys.size - 1
    while (i <= listOfKeys.size - 1 && j >= 0){
        codedMap[listOfKeys[j]] = listOfKeys[i]
        i++
        j--
    }

    //reads the .txt file again and writes on the output file the corresponding coded word
    readFileWords(args[0]).forEach { word ->
        if (codedMap.containsKey(word))
            outputFile.println(codedMap[word])
    }
    outputFile.close()

}