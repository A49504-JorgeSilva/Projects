import pt.isel.tds.go.model.*

/**
 * Shows the board.
 */
fun Board.show() {
    require(BOARD_SIZE == 9 || BOARD_SIZE == 13 || BOARD_SIZE == 19) { "Invalid board size" }
    print("    ")
    for (col in 0 until BOARD_SIZE) {
        val colLabel = ('A' + col).toString()
        print("$colLabel ")
    }
    println()
    for (row in BOARD_SIZE - 1 downTo 0) {
        if (BOARD_SIZE>=10 && row >=9) print("${row + 1} ")
        else print(" ${row + 1} ")
        for (col in 0 until BOARD_SIZE) {
            val cell = Position(row, col)
            val player = moves[cell]
            val cellSymbol = when {
                player == Stone.BLACK -> '#'
                player == Stone.WHITE -> 'O'
                else -> '.'
            }
            print(" $cellSymbol")
        }
        println()
    }
}

/**
 * Shows the game.
 */
fun Game.show() {
    if (board != null) {
        board.show()
        when (board) {
            is BoardRun -> println("Turn: ${board.turn.type} (${board.turn})      Captures: #=${score[Stone.BLACK]?.toInt()} - O=${score[Stone.WHITE]?.toInt()}")
            is BoardPass -> println("${board.turn.other().type} Passes. Turn: ${board.turn.other()} (${board.turn.other().type})")
            is BoardFinish -> {
                checkNotNull(board.winner)
                println("GAME OVER      Score: #=${score[Stone.BLACK]} - O=${score[Stone.WHITE]}")
            }
        }
    }
}