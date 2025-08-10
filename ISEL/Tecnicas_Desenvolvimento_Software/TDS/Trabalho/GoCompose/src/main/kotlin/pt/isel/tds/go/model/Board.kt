package pt.isel.tds.go.model

/**
 * Represents board size.
 */
const val BOARD_SIZE = 9

/**
 * Represents a move in the board.
 */
typealias Moves = Map<Position,Stone>

/**
 * Represents a Go board.
 * @property moves The board moves.
 * @property turn The stone turn.
 * @property groups The groups of stones.
 * @property winner The winner stone.
 */
sealed class Board(val moves: Moves)
open class BoardRun(moves: Moves, val turn: Stone, val groups: Set<Group>): Board(moves)
class BoardFinish(moves: Moves, val winner: Stone, val groups: Set<Group>): Board(moves)

class BoardPass(moves: Moves, turn: Stone, groups: Set<Group>): BoardRun(moves, turn, groups)

/**
 * Represents a play information.
 * @property board The board.
 * @property score The score of the play.
 */
data class PlayInfo(val board: Board, val score: Int)

/**
 * Plays a move in the board.
 * @param position The position to play.
 * @return The play information.
 */
fun Board.play(position: Position): PlayInfo = when(this) {
    is BoardRun-> {
        check(moves[position] == null) { "Position used" }
        check(position.isValidMove(moves, this.groups, turn) ) { "Position is not valid (liberty rule)" }
        val moves = moves + (position to turn)
        val group = addToGroup(moves, position, groups, turn)
        val groups = this.groups.filter { it.stone==turn.other() }
        val capturedGroups = groups.filter { group -> group.isGroupCaptured(moves) }
        val newMoves = moves.removeCapturedGroups(capturedGroups.toSet())
        val score = capturedGroups.sumOf { it.positions.size }
        val newGroups: Set<Group> = this.groups + group
        val finalGroups = if(group.positions.size==1) {
            newGroups.filter { it !in capturedGroups }.toSet()
        }
        else {
            val groupsToRemove = newGroups.filter { group.positions.containsAll(it.positions) }.dropLast(1)
            newGroups.filter { it !in capturedGroups }.filterNot { it in groupsToRemove }.toSet()
        }
        when {
            isWin(newMoves) -> {
                val board = BoardFinish(newMoves, turn, finalGroups)
                PlayInfo(board, score)
            }
            else -> PlayInfo(BoardRun(newMoves, turn.other(), finalGroups), score)
        }
    }
    else ->  error("Game is over")
}

/**
 * Removes the captured groups from the board.
 * @param groups The groups to remove.
 * @return The new board.
 */
fun Moves.removeCapturedGroups(groups: Set<Group>): Moves {
    val positionsToRemove = groups.flatMap { it.positions } //flatmap junta os elementos dos grupos
    return this.filterKeys { it !in positionsToRemove }  //remove as posicoes (key)
}


/**
 * Checks if the game is win.
 * @param moves The board moves.
 * @return True if the game is win, false otherwise.
 */
fun BoardRun.isWin(moves: Moves): Boolean = !hasFreePositions(moves)


/**
 * Gets the free positions of the board.
 * @param moves The board moves.
 * @return The free positions.
 */
private fun getFreePositions(moves: Moves): Set<Position> {
    val emptyPositions = Position.values.filter { it !in moves }
    return emptyPositions.toSet()
}

/**
 * Checks if the board has free positions.
 * @param moves The board moves.
 * @return True if the board has free positions, false otherwise.
 */
fun BoardRun.hasFreePositions(moves: Moves): Boolean {
    getFreePositions(moves).forEach { position ->
        if (position.isValidMove(moves, this.groups, this.turn.other())) {
            return true
        }
    }
    return false
}

/**
 * Gets the final score of the board.
 * @param score The current score.
 * @param groups The groups of the board.
 * @param stone The stone.
 * @return The final score.
 */
fun BoardFinish.pointsFinal(score: Score, groups: Set<Group>, stone: Stone): Score {
    val boardTotal = BOARD_SIZE * BOARD_SIZE
    if (numberOfPlayedStones(moves, stone.other()) !=0) {
        return score.entries.associate {
            val capturedScore = score[it.key]
            checkNotNull(capturedScore) {"Points must be positive"}
            val capturedPositions = getFreePositions(this.moves).filter { position ->  !position.isValidMove(moves, groups, it.key.other()) }.size
            it.key to capturedPositions + capturedScore - blackTax(it.key)
        }
    }
    else return score.entries.associate {
        val capturedScore = score[it.key]
        checkNotNull(capturedScore) {"Points must be positive"}
        if (it.key == stone) {
            it.key to boardTotal - numberOfPlayedStones(moves, it.key) + capturedScore -
                    blackTax(it.key)
        } else {
            it.key to numberOfPlayedStones(moves, it.key) + capturedScore -
                    blackTax(it.key)
        }
    }
}

/**
 * Gets the black tax.
 * @param stone The stone.
 * @return The black tax.
 */
private fun blackTax(stone: Stone): Double {
    return if (stone == Stone.BLACK) {
        when (BOARD_SIZE) {
            9 -> 3.5
            13 -> 4.5
            19 -> 5.5
            else -> error("Invalid board size")
        }
    } else 0.0
}

/**
 * Gets the number of played stones.
 * @param moves The board moves.
 * @param stone The stone.
 * @return The number of played stones.
 */
private fun numberOfPlayedStones(moves: Moves, stone: Stone): Int {
    return moves.filter { it.value == stone }.size
}

/**
 * Creates the initial board.
 * @param turn The stone to first play.
 */
fun Board(turn: Stone = Stone.BLACK) = BoardRun(emptyMap(), turn, emptySet())

/**
 * Passes the turn.
 * @return The new board.
 */
fun BoardRun.pass(): Board {
    return if (this is BoardPass) {
        BoardFinish(this.moves, this.turn, this.groups)
    }
    else {
        BoardPass(this.moves, this.turn.other(), this.groups)
    }
}



