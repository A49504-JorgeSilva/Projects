package pt.isel.tds.go

import pt.isel.tds.go.model.Game
import pt.isel.tds.go.model.GameSerializer
import pt.isel.tds.go.storage.TextFileStorage
import pt.isel.tds.go.view.*
import pt.isel.tds.ttt.view.Command
import pt.isel.tds.ttt.view.getCommands
import show

/**
 * The main function of the application.
 * It reads commands from the console and executes them.
 * The only mutable variable is the board state that is updated by the commands.
 * Commands are defined in the [getCommands] function.
 * Exceptions are caught and the error message is printed.
 */
fun main() {
    var game: Game? = Game()
    val storage = TextFileStorage<String, Game>("games", GameSerializer)
    val commands: Map<String, Command> = getCommands(storage)
    while(true) {
        val (name,args) = readCommand()
        val cmd = commands[name]
        if (cmd==null) println("Unknown command")
        else try {
            checkNotNull(game)
            game = cmd.execute(args, game)
            if (cmd.isToFinish) break
        } catch (e: IllegalArgumentException) {
            println("${e.message}\nUse: $name ${cmd.args}")
        } catch (e: IllegalStateException) {
            println(e.message)
        }
        checkNotNull(game)
        game.show()
    }
}
