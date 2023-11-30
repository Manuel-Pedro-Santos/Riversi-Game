package pt.isel.tds.reversi.ui

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.isel.tds.reversi.model.*
import pt.isel.tds.reversi.storage.MongoDriver
import pt.isel.tds.reversi.storage.MongoStorage
import pt.isel.tds.reversi.storage.TextFileStorage

/**
 * View model of the application.
 * Manages the state of the game and open dialogs.
 */

class ViewModel(val scope:CoroutineScope,val storage: BoardStorage) {
    //region Game State
    // private val storage = MongoStorage("games", driver, BoardSerializer))
    var cellsChanged: Set<Cell> = emptySet()
        private set
    var targetsFlag by mutableStateOf(false)
    val isMultiGame get()= game is MultiGame
    var message = ""
        private set


    // Game being played
    var game by mutableStateOf<Game?>(null)
        private set
    val board get() = checkNotNull(game).board



    // Creates a new game with the given name and close the dialog
    fun newGame(name: String, player: Player, multigame: Boolean) =
        tryRun {
            val g = game
            if (g == null || !g.board.isNew) {
                if (multigame) {
                    game = createMultiGame(name, player, storage)
                } else {
                    game = createSingleGame(player)
                }
                targetsFlag = false
                closeDialog()
            }
        }


    // Plays the given position in the game
    fun play(cell: Cell) =
        try {
            val previousBoard = game?.board as BoardRun
            game = game?.play(cell, storage)
            cellsChanged = (previousBoard.moves.keys + (game?.board as BoardRun).moves.keys).filter {
                previousBoard.moves[it] != (game?.board as BoardRun).moves[it] && previousBoard.moves[it] != null
            }.toSet()
            loopAutoRefresh()
        }
        catch (e: Exception) {
            println(e)
        }

     //Pass the game
    fun pass() = tryRun {
        game = game?.pass(storage)
        loopAutoRefresh()
    }


    // Joins the game with the given name and close the dialog
    fun joinGame(name:String) =
        scope.launch {
            tryRunS {
                game = joinGame(name, storage)
                closeDialog()
                loopAutoRefresh()
            }
        }

    //Refresh the game when called
    fun refresh(){
        scope.launch{
            tryRunS {
                game = game?.refresh(storage)
            }
        }
    }

    //Activates or deactivates the targets
    fun targets(){
        if (game == null) return
        //val str = if (targetsFlag) "ON" else "OFF"
        game = game?.targets(targetsFlag,storage)
        targetsFlag = !targetsFlag
    }


    val canRefresh get() = mayRefresh() &&  !autoRefresh
    private fun  mayRefresh():Boolean{
        val g = game?: return false
        //return ((g.board !is BoardWin) && (g.board !is BoardDraw)) || (g.board !is BoardPass)
        return ((g.board !is BoardWin) && (g.board !is BoardDraw)) && g is MultiGame
    }
    //Check if a given piece is to be rotated
    fun cellsRotating(pos: Cell): Boolean {
        return cellsChanged.contains(pos)
    }

    //Checks if cell is a playable cell
    fun cellsTarget(cell: Cell) :Boolean{
        if(!targetsFlag) return false
        return game?.board?.playableCells()?.contains(cell) == true
    }


    fun isGamePass():Boolean {
        return game?.board?.isPass ?: false
    }

    fun isNewGame():Boolean{
        return game?.board?.isNew ?: false
    }

    fun getPoints(player: Player): Int {
        return board.moves.count { it.value == player }

    }

    fun isMyTurn(): Boolean {
        return when (val b = game?.board) {
            is BoardRun -> {
                if (game is MultiGame) {
                    b.turn == (game as MultiGame).player
                } else {
                    false
                }
            }
            else -> false
        }
    }

    //endregion

    //region Refresh State
    var autoRefresh by mutableStateOf(false)
        private set

    /**
     *
     */
    fun toggleAutoRefresh() {
        autoRefresh = !autoRefresh
        if(autoRefresh) loopAutoRefresh()
        else refresh?.let {
            it.cancel()
            refresh = null
        }
    }

    private var refresh : Job? = null
    private fun loopAutoRefresh() {
        if (autoRefresh && mayRefresh()  && refresh == null) {
            refresh = scope.launch {
                while (true){
                    game = game?.refresh(storage,checked = false)
                    if(!mayRefresh()) break
                    delay(2000)
                }
                refresh = null
            }
        }
    }
    //endregion

    //region Dialogs State
    // The dialog opened
    var open by mutableStateOf<Dialog?>(null)
        private set
    fun openDialog(d:Dialog) { open = d }

    private fun openMessageDialog(e: Exception) {
        message = e.message ?: "Unknown error"
        openDialog(Dialog.MESSAGE)
    }
    fun closeDialog() { open = null }
    //endregion

    //region Status information
    // The status of the game to be displayed in status bar
    val status:StatusInfo
        get() = when (val b = game?.board) {
            is BoardRun -> "Turn" of b.turn
            is BoardWin -> "Winner" of b.winner
            is BoardDraw -> "Draw" of null
            null -> "No Game being Played!" of null
        }
    //endregion

    //region Auxiliary Functions
    private fun CoroutineScope.tryRunS(block: suspend()->Unit) =
        launch {
            try { block() } catch (e: IllegalStateException) {
                openMessageDialog(e)
            }
        }

    private inline fun tryRun(block: ()->Unit) =
        try { block() } catch (e: IllegalStateException) { openMessageDialog(e)  }
    //endregion
}


// Dialogs of the application
enum class Dialog { NEW, JOIN,MESSAGE }
// Status of the game to be displayed in status bar
data class StatusInfo(val label: String, val player: Player?)
infix fun String.of(player: Player?) = StatusInfo(this, player)
