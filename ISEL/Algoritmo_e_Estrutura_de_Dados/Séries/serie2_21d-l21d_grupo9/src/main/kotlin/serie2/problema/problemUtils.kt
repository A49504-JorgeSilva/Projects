package serie2

import java.io.File
import java.io.PrintWriter

fun String.filterInvalidWords() =
    this.length > 1 && !this.contains(".") && !this.contains(",") &&
            !this.contains("?") && !this.contains("!") && !this.contains(":")

/**
 * Returns list of strings from file [filename]
 */
fun readFileWords(filename: String) =
    File(filename)
        .readText(Charsets.UTF_8)
        .trim()
        .split(" ")
        .filter { it.filterInvalidWords() }
        .map { it.replace('\n', ' ').replace('\r', ' ') }

fun createWriter(fileName: String?): PrintWriter {
    return PrintWriter(fileName)
}