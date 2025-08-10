package pt.isel.tds.go.viewModel

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.isel.tds.go.model.BoardFinish
import pt.isel.tds.go.model.BoardRun
import pt.isel.tds.go.model.Position
import pt.isel.tds.ttt.tds.ttt.model.*

/**
 * The purpose of name editing
 * @param txt the text to show in the dialog.
 */
enum class InputName(val txt: String) { FOR_START("Start"), FOR_JOIN("Join") }

/**
 * The application view model.
 * @param storage the storage used in matches.
 * @param scope the scope used to launch coroutines.
 */
class AppViewModel(storage: MatchStorage, private val scope: CoroutineScope) {
    private var match by mutableStateOf(Match(storage))     // Model state

    //tabuleiro atual, contendo a posição das pedras
    val board get() = match.let{ (it as? MatchRun)?.game?.board  }

    //Resultado do jogo, com a quantidade de peças capturadas pro cada jogador
    val score get() = match.let{ (it as MatchRun).game.score  }

    //A cor das pedras do jogador atual
    val sidePlayer get() = (match as? MatchRun)?.sideStone

    //O nome do jogo atual
    val gameName get() = (match as MatchRun).id

    //A cor das pedras do primeiro jogador
    private val firstPlayer get() = (match as? MatchRun)?.game?.firstStone

    /*
    A função recebe uma posição e joga a pedra nessa posição, atualizando o estado do jogo.
    Se a jogada for inválida, a função lança uma exceção.
     */
    fun play(pos: Position) {
        try {
            match = match.play(pos)
            waitForOtherPlayer()
        }catch (e: IllegalStateException) {
            message = e.message
        }
    }

    /*
    A função passa a vez ao adversário, sem realizar nenhuma jogada.
    Se n for a vez do jogador, a função lança uma exceção.
     */
    fun pass() {
        try {
            match = match.pass()
            waitForOtherPlayer()
        }catch (e: IllegalStateException) {
            message = e.message
        }
    }

    //Valor booleano que indica se a checkItemBox de capturas está marcada
    var showCaptures by mutableStateOf(false)  // ScoreDialog state
        private set

    //Função que ativa a checkItemBox de capturas
    fun showCaptures() { if (match is MatchRun) showCaptures=true }

    //Função que desativa a checkItemBox de capturas
    fun hideCaptures() { showCaptures=false }

    //Valor booleano que indica se a checkItemBox de score está marcada
    var showScore by mutableStateOf(false) // ScoreDialog state
        private set

    //Função que ativa a checkItemBox de score
    fun showScore() { if (board is BoardFinish) showScore=true }

    //Função que desativa a checkItemBox de score
    fun hideScore() { showScore=false }

    //Um objeto inputName que armazena o nome do jogador que está a digitar
    var inputName by mutableStateOf<InputName?>(null) // StartOrJoinDialog state
        private set

    /*
    A função recebe um inputName e define o nome do jogador atual com esse inputName.
     */
    fun readName(inputName: InputName) { this.inputName = inputName }

    /*
    A função cancela o input do nome do jogador atual, resetando o estado do jogo.
     */
    fun cancelInput() { inputName = null }

    /*
    A função recebe um nome e tenta iniciar ou juntar-se a um jogo com esse nome.
    Se n for possivel iniciar ou juntar-se a um jogo com esse nome, a função lança uma exceção.
     */
    fun startOrJoin(name: Name) {
        cancelWaiting()
        match = if (inputName==InputName.FOR_START)
            match.start(name)
        else
            match.join(name).also { waitForOtherPlayer() }
        inputName = null
    }

    var showLast by mutableStateOf(false)  // ShowLast state
        private set

    /*
    A função alterna a exibição das pedras capturadas.
     */
    fun showLast() { showLast = !showLast }

    var message by mutableStateOf<String?>(null)  // MessageDialog state
        private set

    /*
    A função limpa a mensagem de diálogo.
     */
    fun clearMessage() { message = null }

    /*
    A função encerra o jogo, resetando o estado do jogo e cancelando quaisquer açoes em andamento.
     */
    fun exit() {
        match.exit()
        cancelWaiting()
    }

    private var waitingJob by mutableStateOf<Job?>(null)

    val isWaiting get() = waitingJob != null

    private val newBoardAvailable get() = match is MatchRun && board.let {
        it is BoardRun && it.turn == sidePlayer || it !is BoardRun && firstPlayer == sidePlayer
    }
    private val isThisSidesTurn get() = sidePlayer!=null && (board as? BoardRun)?.turn == sidePlayer || newBoardAvailable

    /*
    Outra função interna que cancela a ação de espera por outro jogador.
     */
    private fun cancelWaiting() {
        waitingJob?.cancel()
        waitingJob = null
    }

    /*
    A função é usada internamente para verificar se é a vez do jogador atual.
    Se n for, ela aguarda um tempo antes de verificar novamente.
     */
    private fun waitForOtherPlayer() {
        if (waitingJob!=null || isThisSidesTurn) return
        waitingJob = scope.launch {
            while (!isThisSidesTurn && match.gameRun?.board is BoardRun) {
                delay(3000)
                try { match = match.refresh() }
                catch (e: NoChangeException) { /* Ignore */ }
                catch (e: Exception) {
                    message = e.message
                    e.printStackTrace()
                    println("Message: ${e.message}")
                    if (e is GameDeletedException) match = Match(match.ms)
                }
            }
            waitingJob = null
        }
    }
}