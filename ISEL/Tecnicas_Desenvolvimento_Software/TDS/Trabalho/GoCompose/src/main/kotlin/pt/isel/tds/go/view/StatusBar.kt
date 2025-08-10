package pt.isel.tds.go.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.isel.tds.go.model.*


val STATUS_BAR_DIM = 50.dp

/**
 * Shows the status bar: Turn, winner or draw.
 * @param board The board with the status to show.
 * @param sideStone The side of the player.
 */
@Composable
fun StatusBar(board: Board?, sideStone: Stone?) {
    Row(
        modifier = Modifier.width(BOARD_DIM+ BOARD_BOARDER_DIM).height(STATUS_BAR_DIM).background(color = Color.LightGray),
        horizontalArrangement = Arrangement.Center,

    ) {
       //println("StatusBar: sidePlayer = $sideStone")
        sideStone?.let {
            Text("You ", style = MaterialTheme.typography.h4)
            Cell( it )
        }
        val (txt, player) = when (board) {
            null -> "No Game" to null
            is BoardPass -> "Pass " to board.turn.other()
            is BoardRun -> "Turn " to board.turn
            is BoardFinish -> "Winner " to board.winner

        }
        Text(txt, style = MaterialTheme.typography.h4)
        player?.let { Cell( it) }
    }
}