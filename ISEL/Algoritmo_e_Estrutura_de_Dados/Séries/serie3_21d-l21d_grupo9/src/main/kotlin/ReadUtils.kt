import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.PrintWriter

fun createReader(fileName: String): BufferedReader {
    return BufferedReader(FileReader(fileName))
}

fun createWriter(fileName: String?): PrintWriter {
    return PrintWriter(fileName)
}