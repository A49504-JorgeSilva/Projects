package pt.isel.tds.go.model

import java.lang.IllegalArgumentException

/**
 * Represents all the possible directions.
 */
enum class Direction(val difRow: Int, val difCol: Int) {
    UP(1,0), DOWN(-1,0), LEFT(0,-1),
    RIGHT(0,1)
}

/**
 * Represents a position of the board.
 * @property row The row.
 * @property col The column.
 */
class Position private constructor(val row: Row, val col: Column) {
    val rowIndex = row.index
    val colIndex = col.index
    override fun toString(): String = "${row.n}${col.char}"

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Position) {
            return false
        }
        return this.row == other.row && this.col == other.col
    }
    companion object{
        val values = List(BOARD_SIZE * BOARD_SIZE) { Position(Row.values[it / BOARD_SIZE], Column.values[it % BOARD_SIZE]) }
        val INVALID =  Position(-1, -1)
        operator fun invoke(row: Row, col: Column) = run {
            require(row in Row.values && col in Column.values)
            values[row.index * BOARD_SIZE + col.index]
        }

        fun valueOf(position: String): Position {
            val row = position[0].toString().toInt().toRow()
            val col = position[1].toColumn()
            return values[row.index * BOARD_SIZE + col.index]
        }
    }
     fun plus(other: Any?): Any {
        if (other is Direction) {
            return Position(this.rowIndex + other.difRow, this.colIndex + other.difCol)
        }
        if (other is Position) {
            return Position(this.rowIndex + other.rowIndex, this.colIndex + other.colIndex)
        }
        throw IllegalArgumentException("Invalid argument")
    }

    override fun hashCode(): Int {
        var result = row.hashCode()
        result = 31 * result + col.hashCode()
        result = 31 * result + rowIndex
        result = 31 * result + colIndex
        return result
    }

}

/**
 * Creates a position.
 * @param row The row.
 * @param col The column.
 * @return The position.
 */
fun Position(row: Int, col: Int): Position =
    if (row in Row.values.indices && col in Column.values.indices) {
        Position.values[row * BOARD_SIZE + col]
    } else {
        Position.INVALID
    }


/**
 * Converts a string to a position.
 * @return The position or null if the string is invalid.
 */
fun String.toPositionOrNull(): Position? = run {
    when (this.length) {
        2 -> {
            val firstChar = this[0]
            val secondChar = this[1]

            val row = firstChar.toString().toIntOrNull()?.toRowOrNull() ?: secondChar.toString().toIntOrNull()?.toRowOrNull() ?: return null
            val col = firstChar.toColumnOrNull() ?: secondChar.toColumnOrNull() ?: return null

            return Position.values[row.index * BOARD_SIZE + col.index]
        }
        3 -> {
            val firstChar = this[0]
            val secondChar = this[1]
            val thirdChar = this[2]

            val row = (firstChar.toString() + secondChar.toString()).toIntOrNull()?.toRowOrNull() ?: return null
            val col = thirdChar.toColumnOrNull() ?: return null

            return Position.values[row.index * BOARD_SIZE + col.index]

        }
        else -> return null
    }
}

/**
 * Checks if the position is a valid move.
 * @param moves The board moves.
 * @param groups The groups.
 * @param stone The stone.
 * @return True if the position is a valid move, false otherwise.
 */
fun Position.isValidMove(moves: Moves, groups: Set<Group>, stone: Stone): Boolean {
    val newMoves = moves + (this to stone) //adiciona a nova jogada
    val group = addToGroup(newMoves, this, groups, stone)
    return !group.isGroupCaptured(newMoves)
}

/**
 * Checks if the position is captured.
 * @param moves The board moves.
 * @return True if the position is captured, false otherwise.
 */
fun Position.isCaptured(moves: Moves): Boolean {
    for (adjacent in this.adjacents()) {
        if (moves[adjacent] == null) {
            return false
        }
    }
    return true
}

/**
 * Gets the adjacent positions.
 * @return The adjacent positions.
 */
private fun Position.adjacents(): List<Position> {
    // Define os deslocamentos para as posições adjacentes
    val above = (this + Direction.UP)//it.row.index <= BOARD_SIZE
    val below = (this + Direction.DOWN)
    val left = (this + Direction.LEFT)
    val right = (this + Direction.RIGHT)

    return listOfNotNull(above, below, left, right)
}

/**
 * Gets the surrounded position.
 * @param position The position.
 * @param expression The expression.
 * @return The surrounded position or null if the position is not surrounded.
 */
fun getSurrounded(position: Position?, expression: () -> Boolean): Position? {

    return if (expression()) {
        position
    }
    else
        null
}

operator fun Position.plus(dir: Direction) = Position(this.rowIndex + dir.difRow, this.colIndex + dir.difCol)

operator fun Position.minus(other: Position): Direction = Direction.entries.first { it.difRow == other.rowIndex - rowIndex && it.difCol == other.colIndex - colIndex }

operator fun Position.minus(dir: Direction) = Position(this.rowIndex - dir.difRow, this.colIndex - dir.difCol)
