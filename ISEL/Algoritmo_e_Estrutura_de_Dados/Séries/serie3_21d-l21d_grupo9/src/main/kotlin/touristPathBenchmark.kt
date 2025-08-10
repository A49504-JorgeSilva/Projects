import kotlin.system.measureTimeMillis

fun touristPathBenchMark(args: Array<String>){

    if (args.size != 1)
        throw Exception("Put the name of a single file in the arguments")

    val fileToRead = args[0]

    do {
        println("----------------")
        println("Commands list: ")
        println("-type [path id1 id2 output.txt] to get the shortest path and cost between id1 and id2 in the output.txt")
        println("-type [e] to exit")
        println("----------------")
        print("Enter command: ")
        val input = readln().split(' ')

        when (input[0]){
            "path" ->
                if(input.size < 4){
                    println("Please insert a valid command [path id1 id2 output.txt] or [e]")
                } else {

                    val elapsed = measureTimeMillis {

                        //read and store values from the console
                        val id1 = input[1]
                        val id2 = input[2]
                        val outputFileName = input[3]

                        //get the graph with the data from the read file
                        val graph = createGraph(fileToRead)

                        //get the value of the shortest distance, as well as the
                        val shortestDistance = shortestDistanceAndPath(graph, id1, id2).first
                        val shortestPath = shortestDistanceAndPath(graph, id1, id2).second

                        val outputFile = createWriter(outputFileName)

                        outputFile.println("The distance between $id1 and $id2 costs $shortestDistance and is:")
                        shortestPath.forEach { it ->
                            outputFile.println(it)
                        }
                        outputFile.close()
                    }
                    println("The program took ${elapsed}ms to execute ")
                }
            "e" -> break
            else -> println("Please insert a valid command [path id1 id2 output.txt] or [e]")
        }
    } while (true)
}