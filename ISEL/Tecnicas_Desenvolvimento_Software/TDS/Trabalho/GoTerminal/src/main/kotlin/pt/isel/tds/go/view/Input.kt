package pt.isel.tds.go.view

/**
 * Represents a command read from the input.
 * @property name The name of the command.
 * @property args The arguments of the command.
 */
data class LineCommand(val name: String, val args: List<String>)

/**
 * Reads a command from the input.
 * If the line is empty, it reads another command.
 * @return The command read.
 */
tailrec fun readCommand(): LineCommand {
    print("> ")
    val line = readln().uppercase().split(' ').filter { it.isNotEmpty() }
    return if (line.isEmpty()) readCommand()
    else LineCommand(line[0], line.drop(1))
}