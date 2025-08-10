import series.serie3.graphCollections.Graph
import java.util.*
import series.serie3.graphCollections.GraphStructure
import kotlin.system.measureTimeMillis

fun main(args:Array<String> ) {
    //touristPath(args)
    touristPathBenchMark(args)
}

data class VertexData(val distance:Int, val previous: GraphStructure.Vertex<Int, VertexData>?)

fun touristPath(args: Array<String>){

    if (args.size != 1)
        throw Exception("Put the name of a single file in the arguments")

    val fileToRead = args[0]

    do {
        println("----------------")
        println("Commands list: ")
        println("-type [path id1 id2 output.txt] to get the shortest path between id1 and id2 in the output.txt")
        println("-type [e] to exit")
        println("----------------")
        print("Enter command: ")
        val input = readln().split(' ')

        when (input[0]){
            "path" ->
                if(input.size < 4){
                    println("Please insert a valid command [path id1 id2 output.txt] or [e]")
                } else {

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

                    //Writes in the output file the shortest distance and the path
                    outputFile.println("The distance between $id1 and $id2 costs $shortestDistance and is:")
                    shortestPath.forEach { it ->
                        outputFile.println(it)
                    }
                    outputFile.close()
                }
            "e" -> break
            else -> println("Please insert a valid command [path id1 id2 output.txt] or [e]")
        }
    } while (true)
}

fun createGraph(input: String): GraphStructure<String, VertexData>{
    val graph = GraphStructure<String, VertexData>()

    val fileToRead = createReader(input)

    //Reads each line of the file. When the line starts with an "a", it adds to the graph the
    // nodes with the corresponding distance in between
    fileToRead.forEachLine { line ->
        val values = line.split(" ")
        when(line[0]){
            'a' -> {
                val id1 = values[1]
                val id2 = values[2]
                val distance = values[3].toInt()

                graph.addVertex(id1, VertexData(distance,null))
                graph.addVertex(id2, VertexData(distance,null))
                graph.addEdge(id1, id2, distance)
            }
        }
    }
    return graph
}

fun shortestDistanceAndPath(graph: GraphStructure<String, VertexData>, start: String, end: String): Pair<Int, List<String>> {
    val queue = LinkedList<String>()
    val visited = HashSet<String>()
    val distances = HashMap<String, Int>()
    val previous = HashMap<String, String>()

    queue.add(start)
    visited.add(start)
    distances[start] = 0

    while (queue.isNotEmpty()) {
        val vertex = queue.poll()
        val neighbors = graph.getVertex(vertex)?.getAdjacencies()
        for (neighbor in neighbors!!) {
            if (!visited.contains(neighbor!!.adjacent)) {
                queue.add(neighbor.adjacent)
                visited.add(neighbor.adjacent)
                distances[neighbor.adjacent] = distances[vertex]!! + graph.getEdge(vertex, neighbor.adjacent)?.weight!!
                previous[neighbor.adjacent] = vertex
            }
        }
    }

    val path = mutableListOf<String>()
    var current = end
    while (current != start) {
        path.add(current)
        current = previous[current] ?: break
    }
    path.add(start)
    path.reverse()

    return Pair(distances[end] ?: -1, path)
}