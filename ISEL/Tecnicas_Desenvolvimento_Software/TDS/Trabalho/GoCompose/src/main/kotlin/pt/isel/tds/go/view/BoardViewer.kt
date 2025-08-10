package pt.isel.tds.go.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import pt.isel.tds.go.model.*


val CELL_DIM = 40.dp
val GRID_LINE_DIM = 1.dp
val BOARD_DIM = GRID_LINE_DIM * (BOARD_SIZE -1) + BOARD_SIZE * CELL_DIM
val BOARD_BOARDER_DIM = 20.dp

/**
 * Shows the board.
 * @param moves The moves to show.
 * @param showLast If the last move should be shown.
 * @param onClick The action to execute when a position is clicked.
 */
@Composable
fun BoardViewer(moves: Moves?, showLast: Boolean, onClick: (Position) -> Unit) {
    val lastMove = if (moves.isNullOrEmpty()) null else moves.keys.last()
    Column(
        modifier = Modifier.size(BOARD_DIM + BOARD_BOARDER_DIM).background(color = Color.Transparent),

        ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = BOARD_BOARDER_DIM),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            repeat(BOARD_SIZE) { column ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.width(CELL_DIM).height(BOARD_BOARDER_DIM)
                ) {
                    val col = (65 + column).toChar()
                    Text(
                        text = col.toString(),
                        color = Color.Black
                    )
                }
            }
        }
        repeat(BOARD_SIZE) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.width(BOARD_BOARDER_DIM).height(CELL_DIM)
                ) {
                    Text(
                        text = (BOARD_SIZE - row).toString(),
                        color = Color.Black
                    )
                }
                repeat(BOARD_SIZE) { col ->
                    val pos = Position(row, col)
                    Cell(
                        stone = moves?.get(pos) ,
                        show = pos == lastMove && showLast,
                        cellBoardPosition = when {
                            row == 0 && col == 0 -> CellBoardPosition.TOP_LEFT
                            row == 0 && col == BOARD_SIZE - 1 -> CellBoardPosition.TOP_RIGHT
                            row == BOARD_SIZE - 1 && col == 0 -> CellBoardPosition.BOTTOM_LEFT
                            row == BOARD_SIZE - 1 && col == BOARD_SIZE - 1 -> CellBoardPosition.BOTTOM_RIGHT
                            row == 0 -> CellBoardPosition.TOP
                            row == BOARD_SIZE - 1 -> CellBoardPosition.BOTTOM
                            col == 0 -> CellBoardPosition.LEFT
                            col == BOARD_SIZE - 1 -> CellBoardPosition.RIGHT
                            else -> CellBoardPosition.CENTER
                        },
                    ){
                        onClick(pos)
                    }
                }
            }
        }
    }
}