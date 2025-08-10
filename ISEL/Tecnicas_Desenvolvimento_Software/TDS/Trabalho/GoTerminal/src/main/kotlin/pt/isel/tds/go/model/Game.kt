package pt.isel.tds.go.model

typealias Score = Map<Stone,Double>


/**
 * Represents the game state.
 * The board, the first stone and the score.
 * @property board The current board.
 * @property firstStone The first stone to next board.
 * @property score The score of the game (number of wins for each player).
 */
data class Game(
    val board: Board? = null,
    val firstStone: Stone = Stone.BLACK,
    val score: Score = (Stone.entries).associateWith { 0.toDouble() }
)

/**
 * Makes a move in the game.
 * @param pos The position to play.
 * @return The new game state.
 */

fun Game.play(pos: Position): Game {
    checkNotNull(board) { "Game is not started" }
    val turn = when (board) {
        is BoardRun -> board.turn
        is BoardFinish -> error("Game is over")
    }
    val playInfo = board.play(pos)
    return when (playInfo.board) {
        is BoardRun -> {
            val score = score.advance(turn, playInfo.score)
            copy(board = playInfo.board, score = score)
        }
        is BoardFinish -> {
            val winner = playInfo.board.winner
            val score = playInfo.board.pointsFinal(score, playInfo.board.groups, winner)
            copy(board = playInfo.board, score = score)
        }
    }
}

/**
 * Advances the score of the stone.
 * @param stone The stone.
 * @param score The score to advance.
 * @return The new score.
 */
private fun Score.advance(stone: Stone, score: Int): Score {
    val oldScore = this[stone]
    checkNotNull(oldScore) { "Score must be positive"}
    val newScore = oldScore + score
    return this + (stone to newScore)
}

/**
 * Creates a new board.
 * @return The new game state.
 */
fun Game.newBoard() = Game(
    board = Board(firstStone),
    firstStone = firstStone,
    score = (Stone.entries).associateWith { 0.toDouble() }
)

