package pt.isel.tds.go.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pt.isel.tds.go.model.Stone

/**
 * The different positions of a cell in the board.
 */
enum class CellBoardPosition { TOP_LEFT, TOP, TOP_RIGHT, LEFT, CENTER, RIGHT, BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT }

/**
 * The cell in the board.
 * @param stone The stone to show in the cell.
 * @param cellBoardPosition The position of the cell in the board.
 * @param show If the cell should show the stone last move.
 * @param onClick The action to execute when cell is clicked.
 */
@Composable
fun Cell(
    stone: Stone?,
    cellBoardPosition: CellBoardPosition = CellBoardPosition.CENTER,
    show: Boolean = false,
    onClick: () -> Unit = {}
) {
    if (stone == null)
        Box(Modifier
            .size(CELL_DIM)
            .clickable(onClick = onClick)
            .background(color = Color.Transparent)
            .drawWithContent {
                when (cellBoardPosition) {
                    CellBoardPosition.TOP_LEFT -> drawTopLeft(this)
                    CellBoardPosition.TOP -> drawTop(this)
                    CellBoardPosition.TOP_RIGHT -> drawTopRight(this)
                    CellBoardPosition.LEFT -> drawLeft(this)
                    CellBoardPosition.CENTER -> drawCenter(this)
                    CellBoardPosition.RIGHT -> drawRight(this)
                    CellBoardPosition.BOTTOM_LEFT -> drawBottomLeft(this)
                    CellBoardPosition.BOTTOM -> drawBottom(this)
                    CellBoardPosition.BOTTOM_RIGHT -> drawBottomRight(this)
                }
            })
    else {
        val borderColor = if (show) Color.Red else Color.Transparent
        Image(
            painterResource(
                if (stone == Stone.BLACK) "blackStone.png"
                else "whiteStone.png"
            ),
            contentDescription = null,
            modifier = Modifier
                .size(CELL_DIM)
                .border(1.dp, borderColor)
        )
    }
}

/**
 * Draws the top left grid lines in the cell.
 * @param drawScope The draw scope.
 * @param stroke The stroke of the line.
 */
fun drawTopLeft(drawScope: DrawScope, stroke: Float = GRID_LINE_DIM.value) {
    val size = drawScope.size
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(size.width/2, size.height/2),
        end = Offset(size.width, drawScope.size.height/2),
        strokeWidth = stroke
    )
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(size.width/2, size.height/2),
        end = Offset(size.width/2, size.height),
        strokeWidth = stroke
    )
}

/**
 * Draws the top grid lines in the cell .
 * @param drawScope The draw scope.
 * @param stroke The stroke of the line.
 */
fun drawTop(drawScope: DrawScope, stroke: Float = GRID_LINE_DIM.value) {
    val size = drawScope.size
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(0f, size.height/2),
        end = Offset(size.width, size.height/2),
        strokeWidth = stroke
    )
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(size.width/2, size.height/2),
        end = Offset(size.width/2, size.height),
        strokeWidth = stroke
    )
}

/**
 * Draws the top right grid lines in the cell.
 * @param drawScope The draw scope.
 * @param stroke The stroke of the line.
 */
fun drawTopRight(drawScope: DrawScope, stroke: Float = GRID_LINE_DIM.value) {
    val size = drawScope.size
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(0f, size.height/2),
        end = Offset(size.width/2, size.height/2),
        strokeWidth = stroke
    )
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(size.width/2, size.height/2),
        end = Offset(size.width/2, size.height),
        strokeWidth = stroke
    )
}

/**
 * Draws the left grid lines in the cell.
 * @param drawScope The draw scope.
 * @param stroke The stroke of the line.

 */
fun drawLeft(drawScope: DrawScope, stroke: Float = GRID_LINE_DIM.value) {
    val size = drawScope.size
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(size.width/2, size.height/2),
        end = Offset(size.width, size.height/2),
        strokeWidth = stroke
    )
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(size.width/2, 0f),
        end = Offset(size.width/2, size.height),
        strokeWidth = stroke
    )
}

/**
 * Draws the center grid lines in the cell.
 * @param drawScope The draw scope.
 * @param stroke The stroke of the line.

 */
fun drawCenter(drawScope: DrawScope, stroke: Float = GRID_LINE_DIM.value) {
    val size = drawScope.size
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(0f, size.height/2),
        end = Offset(size.width, drawScope.size.height/2),
        strokeWidth = stroke
    )
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(size.width/2, 0f),
        end = Offset(size.width/2, size.height),
        strokeWidth = stroke
    )
}

/**
 * Draws the right grid lines in the cell.
 * @param drawScope The draw scope.
 * @param stroke The stroke of the line.


 */
fun drawRight(drawScope: DrawScope, stroke: Float = GRID_LINE_DIM.value) {
    val size = drawScope.size
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(0f, size.height/2),
        end = Offset(size.width/2, size.height/2),
        strokeWidth = stroke
    )
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(size.width/2, 0f),
        end = Offset(size.width/2, size.height),
        strokeWidth = stroke
    )
}

/**
 * Draws the bottom left grid lines in the cell.
 * @param drawScope The draw scope.
 * @param stroke The stroke of the line.
 */
fun drawBottomLeft(drawScope: DrawScope, stroke: Float = GRID_LINE_DIM.value) {
    val size = drawScope.size
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(size.width/2, size.height/2),
        end = Offset(size.width, drawScope.size.height/2),
        strokeWidth = stroke
    )
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(size.width/2, size.height/2),
        end = Offset(size.width/2, 0f),
        strokeWidth = stroke
    )
}

/**
 * Draws the bottom grid lines in the cell.
 * @param drawScope The draw scope.
 * @param stroke The stroke of the line.
 */
fun drawBottom(drawScope: DrawScope, stroke: Float = GRID_LINE_DIM.value) {
    val size = drawScope.size
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(0f, size.height/2),
        end = Offset(size.width, size.height/2),
        strokeWidth = stroke
    )
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(size.width/2, 0f),
        end = Offset(size.width/2, size.height/2),
        strokeWidth = stroke
    )
}

/**
 * Draws the bottom right grid lines in the cell.
 * @param drawScope The draw scope.
 * @param stroke The stroke of the line.
 */
fun drawBottomRight(drawScope: DrawScope, stroke: Float = GRID_LINE_DIM.value) {
    val size = drawScope.size
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(0f, size.height/2),
        end = Offset(size.width/2, size.height/2),
        strokeWidth = stroke
    )
    drawScope.drawLine(
        color = Color.Black,
        start = Offset(size.width/2, 0f),
        end = Offset(size.width/2, size.height/2),
        strokeWidth = stroke
    )
}