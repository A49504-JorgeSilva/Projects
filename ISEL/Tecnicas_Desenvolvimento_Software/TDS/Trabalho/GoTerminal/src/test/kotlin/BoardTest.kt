
import pt.isel.tds.go.model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BoardTest {
    @Test
    fun testValidMove() {
        val initialBoard = Board()
        val position = Position(0, 0)
        val validMove = position.isValidMove(initialBoard.moves, emptySet(), Stone.BLACK)
        assertTrue(validMove)
    }

    @Test
    fun testAddToGroup() {
        val initialBoard = Board()
        val moves = initialBoard.moves + (Position(0, 0) to Stone.BLACK) + (Position(1, 0) to Stone.BLACK)
        val group = addToGroup(moves, Position(0, 0), emptySet(), Stone.BLACK)
        assertEquals(1, group.positions.size) // Expect 1 because addToGroup returns a group with a single position
        assertEquals(Stone.BLACK, group.stone)
    }

        @Test
    fun testIsNotGroupCaptured() {
        val moves = mapOf(
            Position(0, 0) to Stone.BLACK,
            Position(1, 0) to Stone.BLACK
        )
        val group = Group(setOf(Position(0, 0), Position(1, 0)), Stone.BLACK)
        assertFalse(group.isGroupCaptured(moves))
    }

    @Test
    fun testIsCaptured() {
        val moves = mapOf(
            Position(0, 0) to Stone.WHITE,
            Position(0, 1) to Stone.WHITE,
            Position(1, 0) to Stone.BLACK,
            Position(1, 1) to Stone.BLACK
        )
        val position = Position(0, 0)
        assertTrue(position.isCaptured(moves))
    }

    @Test
    fun testHasFreePositions() {
        val moves = mapOf(
            Position(0, 0) to Stone.BLACK,
            Position(1, 0) to Stone.WHITE,
            Position(0, 1) to Stone.BLACK
        )
        val board = BoardRun(moves, Stone.WHITE, emptySet())
        assertTrue(board.hasFreePositions(moves))
    }

    @Test
    fun `Test no available positions`() {
        val board = Board()
        val moves = mutableMapOf<Position, Stone>()

        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                val position = Position(row, col)
                val stone = if ((row + col) % 2 == 0) Stone.BLACK else Stone.WHITE
                moves[position] = stone
            }
        }
        assertFalse(board.hasFreePositions(moves))
    }

    @Test
    fun `Test liberty rule`() {
        val moves = mapOf(
            Position(0, 1) to Stone.WHITE,
            Position(1, 0) to Stone.WHITE
        )

        assertFalse(Position(0, 0).isValidMove(moves, emptySet(), Stone.BLACK))
    }
}