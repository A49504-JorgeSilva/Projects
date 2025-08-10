package pt.isel.tds.ttt.view

import pt.isel.tds.go.model.*
import pt.isel.tds.go.storage.Storage


/**
 * Represents a command that can be executed in the application.
 * @property args The syntax of the command arguments.
 * @property isToFinish Indicates if the command finishes the application.
 * @property execute The function that executes the command.
 */
class Command(
    val args:String = "",
    val isToFinish: Boolean = false,
    val execute: (args: List<String>, Game) -> Game? = { _, g -> g }
)

/**
 * Represents a command that plays a position.
 */
val Play = Command("<position>") { args, game ->
    require(args.isNotEmpty()) { "Missing position" }
    //obtem a parte inteira da string
    val pos = requireNotNull(args[0].uppercase().toPositionOrNull()) { "Invalid position ${args[0]}"}
    game.play(pos)
}

/**
 * Returns a map of all commands supported by the application.
 */
fun getCommands(st: Storage<String, Game>): Map<String, Command> = mapOf(
    "NEW" to Command { _, game -> game.newBoard() },
    "PLAY" to Play,
    "EXIT" to Command(isToFinish = true),
    "SAVE" to Command("<name>") { args, game ->
        require(args.isNotEmpty()) { "Missing name" }
        val name = args[0]
        require(name.isNotEmpty()) { "Name can not be empty" }
        st.create(name, game)
        println("Game saved")
        game
    },
    "LOAD" to Command("<name>") { args, _ ->
        require(args.isNotEmpty()) { "Missing name" }
        val name = args[0]
        require(name.isNotEmpty()) { "Name can not be empty" }
        val readedGame = st.read(name)
        checkNotNull(readedGame) { "Game not found" }
        readedGame
    },
    "PASS" to Command { _, game ->
        val board = game.board
        checkNotNull(board){ "Game is not started"}
        check(board is BoardRun) { "Game is not running" }
        val newBoard = board.pass()
        if (newBoard is BoardFinish) {
            val winner = newBoard.winner
            val score = newBoard.pointsFinal(game.score, newBoard.groups, winner)
            game.copy(board = newBoard, score = score)
        } else {
            game.copy(board = newBoard)
        }
    }
)