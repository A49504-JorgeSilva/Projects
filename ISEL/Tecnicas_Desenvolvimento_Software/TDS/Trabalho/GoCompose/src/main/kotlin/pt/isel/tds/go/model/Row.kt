package pt.isel.tds.go.model

/**
 * Represents a row of the board.
 * @property n The row number.
 */
class Row private constructor(val n: Int) {
    val index = n - 1
    override fun toString() = "Row $n"
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Row) {
            return false
        }
        return this.n == other.n && this.index == other.index
    }

    operator fun minus(i: Int): Row {
        require(i in 1 .. BOARD_SIZE)
        return values[n - i]
    }

    operator fun plus(i: Int): Row {
        require(i in 1 .. BOARD_SIZE)
        return values[n + i]
    }

    override fun hashCode(): Int {
        var result = n
        result = 31 * result + index
        return result
    }

    companion object {
        val values = List(BOARD_SIZE) { Row(it + 1) }
        operator fun invoke(n: Int) = run {
            require(n in 1 .. BOARD_SIZE)
            values[n - 1]
        }
    }
}

/**
 * Converts an Integer into a row or null.
 */
fun Int.toRowOrNull(): Row? = if (this in 1 .. BOARD_SIZE) Row.values[this - 1] else null
fun Int.toRow(): Row = run{
    require(this in 1 .. BOARD_SIZE){throw IllegalArgumentException("Invalid row $this")}
    Row.values[this - 1]
}
