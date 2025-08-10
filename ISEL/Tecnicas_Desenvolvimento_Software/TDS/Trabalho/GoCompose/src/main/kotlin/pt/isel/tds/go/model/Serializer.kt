package pt.isel.tds.go.model

import pt.isel.tds.storage.Serializer

/**
 * Serializer for the game.
 */
object GameSerializer: Serializer<Game> {
    override fun serialize(data: Game): String = buildString {
        appendLine(data.score.entries.joinToString(" ") { "${it.key}:${it.value}" }) //pontuações
        appendLine(";")
        if (data.board != null) appendLine(BoardSerializer.serialize(data.board)) //tabuleiro

    }

    override fun deserialize(text: String): Game {
        val textSplitted = text.split(";")
        val score = textSplitted[0].replace("\n", "").split(" ").associate { entry ->
            val params = entry.split(":")
            val stone = Stone.valueOf(params[0])
            val value = params[1].toDouble()
            stone to value
        }
        val board = BoardSerializer.deserialize(textSplitted[1])
        return Game(board, Stone.BLACK, score)
    }
}


/**
 * Serializer for the board.

 */
object BoardSerializer: Serializer<Board> {
    override fun serialize(data: Board): String {
        // moves
        val moves = data.moves
        val movesString =
            moves.entries.joinToString("\n") { "${it.key}:${it.value}" } //uma jogada e cada linha do ficheiro
        val boardType = when (data) {
            is BoardRun -> {
                if (data is BoardPass) BoardPass::class.simpleName
                else BoardRun::class.simpleName
            }
            is BoardFinish -> BoardFinish::class.simpleName
        }
        when (data) {
            is BoardRun -> {
                val groups = data.groups
                val turn = data.turn
                val groupsString = groups.joinToString("\n") { "${it.stone}:${it.positions.joinToString(" ")}" }
                return buildString {
                    appendLine(boardType)
                    appendLine(".")
                    appendLine(movesString)
                    appendLine(".")
                    appendLine(groupsString)
                    appendLine(".")
                    appendLine(turn)
                }
            }

            is BoardFinish -> {
                val winner = data.winner
                val groups = data.groups
                val groupsString = groups.joinToString("\n") { "${it.stone}:${it.positions.joinToString(" ")}" }
                return buildString {
                    appendLine(boardType)
                    appendLine(".")
                    appendLine(movesString)
                    appendLine(".")
                    appendLine(groupsString)
                    appendLine(".")
                    appendLine(winner)
                }
            }
        }
    }


    override fun deserialize(text: String): Board {
        //println("Text: " + text)
        val params = text.split(".")
        val boardType = params[0].replace("\n", "")
        val movesList = params[1].split("\n").filter { it != "" }
        val moves = movesList.associate {
            val (position, stone) = it.split(":")
            (Position.valueOf(position)) to
                    Stone.valueOf(stone)
        }
        val groupsList = params[2].split("\n").filter { it != "" }
        //println("Params: $params")
        //println("groupsList: $groupsList")
        val groups: Set<Group> = groupsList.map {
            //println("group: $it")
            val (stone, positions) = it.split(":")
            Group(stone= Stone.valueOf(stone),
                positions = positions.split(" ").map { position -> Position.valueOf(position) }.toSet())
        }.toSet()
        val turnOrWinner = Stone.valueOf(params[3].replace("\n", ""))
        return when (boardType){
            BoardRun::class.simpleName -> BoardRun(moves, turnOrWinner, groups)
            BoardPass::class.simpleName -> BoardPass(moves, turnOrWinner, groups)
            BoardFinish::class.simpleName -> BoardFinish(moves, turnOrWinner, groups)
            else -> error("Invalid board type")
        }
    }
}