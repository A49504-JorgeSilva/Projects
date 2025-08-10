package pt.isel.tds.go.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isel.tds.go.model.*
import pt.isel.tds.ttt.tds.ttt.model.Name


/**
 * Dialog to show the score.
 * @param score The score to show.
 * @param gameName The name of the game.
 * @param onClose The action to execute when dialog is closed.
 */

/*
A função exibe uma caixa de diálogo com o título "Captures of $gameName" e o conteúdo é uma coluna com as peças e o número de capturas de cada uma.
Mostra a quantidade de pedras capturadas por cada jogador.
A pontuação de cada cor é obtida através da função score.
 */
@Composable
fun CapturesViewer(score: Score, gameName: Name, onClose: () -> Unit) = BaseDialog(
    title = "Captures of $gameName",
    content = {
        Row(
            Modifier.fillMaxWidth().padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Stone.entries.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Cell( stone = it)
                        Text(" - ${score[it]?.toInt()}", style = MaterialTheme.typography.h4)
                    }
                }
            }
        }
    },
    onClose = onClose
)

/**
 * Dialog to show the score.
 * @param score The score to show.
 * @param board The board to calculate the score.
 * @param gameName The name of the game.
 * @param onClose The action to execute when dialog is closed.
 */

/*
A função exibe uma caixa de diálogo com o título "Score of $gameName" e o conteúdo é uma coluna com as peças e o número de pontos de cada uma.
Mostra a pontuação de cada jogador que é obtida através do objeto score.
A pontuação final é calculada com base nas pedras capturadas e nas pedras vazias
 */
@Composable
fun ScoreViewer(score: Score, board: Board?, gameName: Name, onClose: () -> Unit) = BaseDialog(

    title = "Score of $gameName",
    content = {
        Row(
            Modifier.fillMaxWidth().padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Stone.entries.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Cell( stone = it)
                        check(board is BoardFinish)
                        Text(" - ${board.pointsFinal(score, board.groups, it)[it]}", style = MaterialTheme.typography.h4)
                    }
                }
            }
        }
    },
    onClose = onClose
)

/**
 * Dialog to show the score.
 * @param message The message to show.
 * @param onClose The action to execute when dialog is closed.
 */

/*
A função exibe uma caixa de diálogo com o título "Error" e o conteúdo é uma coluna com a mensagem de erro.
Recebe uma mensagem de erro e exibe-a numa caixa de diálogo.
 */
@Composable
fun MessageDialog(message: String, onClose: () -> Unit) = BaseDialog(
    title = "Error",
    content = { Text(message, style = MaterialTheme.typography.h4) },
    onClose = onClose
)

/**
 * Base dialog.
 * @param title The title of the dialog.
 * @param content The content of the dialog.
 * @param onClose The action to execute when dialog is closed.
 */

/*
A função é a base pra todas as outras funções de caixa de dialogo.
Recebe o titulo, o conteudo e a ação a ser executada quando a caixa for fechada.
Usa a função AlertDialog para criar a caixa de dialogo.
 */
@Composable
fun BaseDialog(title: String, content: @Composable () -> Unit, onClose: () -> Unit ) = AlertDialog(
    onDismissRequest = onClose,
    title = { Text(title, style = MaterialTheme.typography.h3, fontSize = 35.sp) },
    text = {
        content()
    },
    confirmButton = { Button(onClick = onClose) { Text("OK") } }
)
