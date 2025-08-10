package pt.isel.tds.ttt.pt.isel.tds.go.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import pt.isel.tds.go.model.BoardFinish
import pt.isel.tds.go.view.*
import pt.isel.tds.go.viewModel.AppViewModel
import pt.isel.tds.go.viewModel.InputName
import pt.isel.tds.ttt.tds.ttt.model.MatchStorage
import pt.isel.tds.ttt.tds.ttt.model.Name


/**
 * Shows the game.
 * @param onExit the function to call on exit the game.
 * @param storage the storage used in matches.
 */
@Composable

/*
Cria a estrutura da janela , que inclui a barra do Menu e a area de jogo.
A função BoardViewer é usada para exibir o tabuleiro.
A função StatusBar é usada para exibir o status do jogo.
 */
fun FrameWindowScope.GoApp(onExit: () -> Unit, storage: MatchStorage) {
    val scope = rememberCoroutineScope()
    val vm = remember { AppViewModel(storage,scope) }
    AppMenu(vm, onExit)
    Box(modifier = Modifier.height(BOARD_DIM + BOARD_BOARDER_DIM+STATUS_BAR_DIM)
        .width(BOARD_DIM + BOARD_BOARDER_DIM)) {
        Image(
            painter = painterResource("board.png"),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
            contentScale = ContentScale.FillBounds
        )
        MaterialTheme {
            Column {
                BoardViewer(vm.board?.moves, vm.showLast) { pos -> vm.play(pos) }
                StatusBar(vm.board, vm.sidePlayer)
                //Quando a caixa de showCaptures for verdadeira, a função CapturesViewer é chamada para exibir a última peça capturada do jogo.
                if (vm.showCaptures) CapturesViewer(vm.score, vm.gameName) { vm.hideCaptures() }
                //Quando a caixa de showScore for verdadeira, a função ScoreViewer é chamada para exibir o resultado do jogo.
                if(vm.showScore) ScoreViewer(vm.score, vm.board, vm.gameName) { vm.hideScore() }
                vm.inputName?.let{ InputNameEdit(it,
                    onAction = { name: Name -> vm.startOrJoin(name) },
                    onCancel = { vm.cancelInput() }
                ) }
                vm.message?.let{ MessageDialog(it) { vm.clearMessage() } }
            }
            //quando a variavel isWaiting for verdadeira, a função WaitingIndicator é chamada para exibir o indicador de espera.
            if (vm.isWaiting) WaitingIndicator()
        }
    }

}

/**
 * Shows the waiting indicator.
 */
@Composable
fun WaitingIndicator() {
    CircularProgressIndicator(Modifier.size(BOARD_DIM).padding(25.dp),
        strokeWidth = 10.dp
    )
}

/**
 * Shows the application menu.
 */
@Composable
fun FrameWindowScope.AppMenu(
    vm: AppViewModel,
    onExit: () -> Unit
) {
    MenuBar {
        Menu("Game") {
            Item("Start game") { vm.readName(InputName.FOR_START) }
            Item("Join game") { vm.readName(InputName.FOR_JOIN) }
            Item("Exit", onClick = {
                vm.exit()
                onExit()
            } )
        }
        Menu("Play") {
            Item("Pass", enabled = !vm.isWaiting, onClick = { vm.pass() } )
            Item("Captures", enabled = vm.board!=null, onClick = vm::showCaptures )
            Item("Score", enabled = vm.board is BoardFinish, onClick = vm::showScore)
        }
        Menu("Options") {
            CheckboxItem("Show last", checked = vm.showLast, onCheckedChange = { vm.showLast() } )
        }
    }
}
