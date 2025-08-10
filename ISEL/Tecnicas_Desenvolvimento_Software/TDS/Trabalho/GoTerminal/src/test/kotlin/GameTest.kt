import pt.isel.tds.go.model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GameTest {
    @Test
    fun blackCaptureInTheCornorOfTheBoard () {
        val plays = listOf("2A", "1A", "1B")
        val playsNotNull = listOfNotNull(plays[0].toPositionOrNull(), plays[1].toPositionOrNull(), plays[2].toPositionOrNull())
        var game = Game().newBoard()
        print(playsNotNull)
        game = game.play(playsNotNull[0])
        game = game.play(playsNotNull[1])
        game = game.play(playsNotNull[2])
        val board = game.board
        check(board is BoardRun)
        game = game.copy(board = board.pass())
        val board2 = game.board
        check(board2 is BoardRun)
        game = game.copy(board = board2.pass())
        val finalBoard = game.board
        assertTrue(finalBoard is BoardFinish)
        game=game.copy(score = finalBoard.pointsFinal(game.score, finalBoard.groups, finalBoard.winner))
        assertTrue(game.score[Stone.BLACK] == 76.5)
        assertTrue(game.score[Stone.WHITE] == 0.0)
    }

    @Test
    fun whiteCaptureInTheCornorOfTheBoard () {
        val plays = listOf("2A", "1A", "1B")
        val playsNotNull = listOfNotNull(plays[0].toPositionOrNull(), plays[1].toPositionOrNull(), plays[2].toPositionOrNull())
        var game = Game().newBoard()
        val initialBoard = game.board
        check(initialBoard is BoardRun)
        game = game.copy(board = initialBoard.pass())
        game = game.play(playsNotNull[0])
        game = game.play(playsNotNull[1])
        game = game.play(playsNotNull[2])
        val board = game.board
        check(board is BoardRun)
        game = game.copy(board = board.pass())
        val board2 = game.board
        check(board2 is BoardRun)
        game = game.copy(board = board2.pass())
        val finalBoard = game.board
        assertTrue(finalBoard is BoardFinish)
        game=game.copy(score = finalBoard.pointsFinal(game.score, finalBoard.groups, finalBoard.winner))
        assertEquals(game.score[Stone.BLACK], -3.5)
        assertTrue(game.score[Stone.WHITE] == 80.0)
    }
    @Test
    fun blackCaptureInTheCornorOfTheBoardWithStoneInBoard () {
        val plays = listOf("2A", "1A","7A","1B")
        val playsNotNull = listOfNotNull(plays[0].toPositionOrNull(), plays[1].toPositionOrNull(), plays[2].toPositionOrNull(), plays[3].toPositionOrNull())
        //new
        var game = Game().newBoard()
        print(playsNotNull)
        //play 2a
        game = game.play(playsNotNull[0])
        //play 1a
        game = game.play(playsNotNull[1])
        //pass
        val board0 = game.board
        check(board0 is BoardRun)
        game = game.copy(board = board0.pass())
        //play 7a
        game = game.play(playsNotNull[2])
        //play 1b
        game = game.play(playsNotNull[3])
        //pass
        val board = game.board
        check(board is BoardRun)
        game = game.copy(board = board.pass())
        //pass dnv
        val board2 = game.board
        check(board2 is BoardRun)
        game = game.copy(board = board2.pass())
        val finalBoard = game.board
        assertTrue(finalBoard is BoardFinish)
        game=game.copy(score = finalBoard.pointsFinal(game.score, finalBoard.groups, finalBoard.winner))
        assertEquals(game.score[Stone.BLACK], -1.5)
        assertTrue(game.score[Stone.WHITE] == 0.0)
    }

    @Test
    fun whiteCaptureInTheCornorOfTheBoardWithStoneInBoard () {
        val plays = listOf("2A", "1A","7A","1B")
        val playsNotNull = listOfNotNull(plays[0].toPositionOrNull(), plays[1].toPositionOrNull(), plays[2].toPositionOrNull(), plays[3].toPositionOrNull())
        //new
        var game = Game().newBoard()
        print(playsNotNull)
        //pass
        game = doPass(game)
        //play 2a
        game = game.play(playsNotNull[0])
        //play 1a
        game = game.play(playsNotNull[1])
        //pass
        val board0 = game.board
        check(board0 is BoardRun)
        game = game.copy(board = board0.pass())
        //play 7a
        game = game.play(playsNotNull[2])
        //play 1b
        game = game.play(playsNotNull[3])
        //pass
        game = doPass(game)
        //pass dnv
        game = doPass(game)
        val finalBoard = game.board
        assertTrue(finalBoard is BoardFinish)
        game=game.copy(score = finalBoard.pointsFinal(game.score, finalBoard.groups, finalBoard.winner))
        assertEquals(game.score[Stone.BLACK], -3.5)
        assertTrue(game.score[Stone.WHITE] == 2.0)
    }

    private fun doPass (game: Game): Game {
        val board = game.board
        check(board is BoardRun)
        return game.copy(board = board.pass())
    }
}