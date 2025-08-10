package pt.isel.tds.go.model

import java.lang.IllegalArgumentException

/**
 * Represents a column of the board.
 * @property char The column char.
 */
class Column private constructor(val char: Char){
    private val symbol = char
    val index = char - 'A'
    override fun toString() = "Column $char"
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Column) {
            return false
        }
        return this.char == other.char && this.index == other.index
    }

    operator fun minus(i: Int): Column {
        require(i in 1 .. BOARD_SIZE)
        return values[char - 'A' - i]
    }

    operator fun plus(i: Int): Column {
        require(i in 1 .. BOARD_SIZE)
        return values[char - 'A' + i]
    }

    override fun hashCode(): Int {
        var result = char.hashCode()
        result = 31 * result + symbol.hashCode()
        result = 31 * result + index
        return result
    }

    companion object{
        val values = List(BOARD_SIZE) { Column('A' + it) }
        operator fun invoke(char: Char) = run {
            require(char in 'A'..'A'+BOARD_SIZE)
            values[char - 'A']
        }
    }
}


/**
 * Converts a char to a column.
 */
fun Char.toColumnOrNull(): Column? = if (this in ('A'..'Z').take(BOARD_SIZE)) Column.values[this - 'A'] else null

/**
 * Converts a char to a column.
 */
fun Char.toColumn():Column = run{
    require(this in 'A'..'A'+ BOARD_SIZE){throw IllegalArgumentException("Invalid column $this")}
    Column.values[this - 'A']
}