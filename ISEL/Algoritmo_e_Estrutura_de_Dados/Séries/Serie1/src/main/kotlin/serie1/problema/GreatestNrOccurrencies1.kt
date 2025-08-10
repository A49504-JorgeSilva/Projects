
package problema

import java.io.BufferedReader
import java.util.*
import kotlin.system.measureTimeMillis

fun main(){
    //greatestNrOccurrencies1()
    greatestNrOccurrencies1BenchMark()
}

data class HeapWords(val string: String, val count: Int): Comparable<HeapWords>{
    override fun compareTo(other: HeapWords): Int{
        return when{
            this.count == other.count -> {
                this.string.compareTo(other.string)
            }
            this.count < other.count -> 1
            else -> -1
        }
    }
}

fun greatestNrOccurrencies1() {
    do {
        print("enter Command: ")
        val fullInput = readln().split(' ')

        when (fullInput[0]){
            "get" ->
                if (fullInput.size <= 3){
                    println("Please insert a valid command with [get] [nrOfWords] [outputFileName.txt] [inputFile.txt]... ")
                } else {
                    val kWords = fullInput[1].toInt()
                    val outputFileName = fullInput[2]
                    val totalInputFiles = fullInput.size - 3
                    val inputFiles = Array<BufferedReader>(totalInputFiles){ createReader(fullInput[it + 3]) }

                    val mappedList = mutableMapOf<String, Int>()

                    val heap = PriorityQueue<HeapWords>()

                    inputFiles.forEach{ file ->
                        file.forEachLine { word ->
                            mappedList[word] = (mappedList.getOrPut(word) { 0 } as Int) + 1
                        }
                    }
                    mappedList.forEach{ entry -> heap.add(HeapWords(entry.key, entry.value))}

                    val kWordsArray = mutableListOf<String>()

                    for (i in 0 until kWords){
                        kWordsArray.add(heap.poll().string)
                    }

                    val outputFile = createWriter(outputFileName)

                    kWordsArray.forEach { word ->
                        outputFile.println(word)
                    }

                    outputFile.close()
                }
            "exit" -> break
            else -> println("Please insert a valid command (get/ exit)")
        }
    } while (true)
}

fun greatestNrOccurrencies1BenchMark(){
    do {
        print("enter Command: ")
        val fullInput = readln().split(' ')

        when (fullInput[0]){
            "get" ->
                if (fullInput.size <= 3){
                    println("Please insert a valid command with [get] [nrOfWords] [outputFileName.txt] [inputFile.txt]... ")
                } else {
                    val elapsed = measureTimeMillis {
                        val kWords = fullInput[1].toInt()
                        val outputFileName = fullInput[2]
                        val totalInputFiles = fullInput.size - 3
                        val inputFiles = Array<BufferedReader>(totalInputFiles){ createReader(fullInput[it + 3]) }

                        val mappedList = mutableMapOf<String, Int>()

                        val heap = PriorityQueue<HeapWords>()

                        inputFiles.forEach{ file ->
                            file.forEachLine { word ->
                                mappedList[word] = (mappedList.getOrPut(word) { 0 } as Int) + 1
                            }
                        }
                        mappedList.forEach{ entry -> heap.add(HeapWords(entry.key, entry.value))}

                        val kWordsArray = mutableListOf<String>()

                        for (i in 0 until kWords){
                            kWordsArray.add(heap.poll().string)
                        }

                        val outputFile = createWriter(outputFileName)

                        kWordsArray.forEach { word ->
                            outputFile.println(word)
                        }

                        outputFile.close()
                    }
                    println(elapsed)
                }
            "exit" -> break
            else -> println("Please insert a valid command (get/ exit)")
        }
    } while (true)
}