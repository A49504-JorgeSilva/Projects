package pt.isel.tds.go.model

/**
 * Represents a stone.
 */
enum class Stone(val type:Char) { BLACK('#'), WHITE('O');
    fun other() = if (this==WHITE) BLACK else WHITE
}
