package pt.isel.tds.reversi.model

//import pt.isel.tds.reversi.ui.BoardStorage
import pt.isel.tds.reversi.storage.Storage

typealias BoardStorage = Storage<String, Board>



sealed class Game(
    val board: Board,
    val target:Boolean,
)
class SingleGame(
    board: Board,
    target: Boolean ,
) : Game(board, target)

class MultiGame(
    board: Board,
    val player: Player,
    val id: String,
    target: Boolean ,
) : Game(board,target)

/**
 * Creates a new game with the given player as the first play.
 */
fun createSingleGame(player: Player):Game =
    SingleGame(board = createBoard(player), target = false)

/**
 * Creates a new game receiving the player and the id of the game.
 */
fun createMultiGame(id: String,player: Player, st: BoardStorage):Game =
    MultiGame(board = createBoard(player), player = player, id = id, target = false).also { st.create(id, it.board) }

/**
 * Checks what type of game we are playing and returns the game of its type.
 * @throws IllegalStateException if it is not his turn to play .
 */
fun Game.play(cell: Cell, st: BoardStorage): Game = when(this) {
    is MultiGame -> {
        check(player == (board as BoardRun).turn) { "Is not your turn" }
        MultiGame( board=board.play(cell),player = player,id,target = target).also { st.update(id, it.board) }
    }
    is SingleGame -> {
        SingleGame( board=board.play(cell),target = target)
    }
}

/**
 * Checks if it needs to be on or off the target mode and returns the game of its type.,
 * with the target mode on or off.
 */
fun Game.targets(flag:String, st: BoardStorage):Game {
    val state = flag == "ON"
    return when (this) {
        is MultiGame -> {
            if(board is BoardRun) check(player == board.turn) { "Is not your turn" }
            MultiGame(board = board, player = player, id = id, target = state).also { st.update(id, it.board) }
        }
        is SingleGame -> SingleGame(board = board, target = state)

    }
}

/**
 * Checks if the game is a MultiGame and returns the game of its type.
 */
fun Game.refresh(st: BoardStorage): Game {
    check( this !is SingleGame) { "WE ARE PLAYING LOCALY!!" }
    return when(this) {
        is MultiGame -> {val board = checkNotNull( st.read(id) ) { "Game not found" }
            check( board != this.board || board !is BoardPass) { "No changes" }
            MultiGame( board = board,player = player ,id = id, target =target)
        }
        else -> this
    }
}

/**
 * Depending on the type of game, it will pass the turn to the other player.
 */
fun Game.pass(st: BoardStorage):Game =
     when (this) {
        is MultiGame -> {
           check(player == (board as BoardRun).turn) { "Is not your turn" }
           MultiGame(board = board.pass(), id = id  ,player =  player, target = target).also { st.update(id, it.board) }
        }
        is SingleGame -> {
                SingleGame(board = board.pass(),target = target)
        }
    }


/**
 * Checks if the game exists in the storage and if it is available to join.
 * @throws IllegalStateException if the game is not available.
 * @throws IllegalStateException if the game does not exist.
 * @return the game joined.
 */
fun joinGame(id: String, st: BoardStorage): Game {
    val board = checkNotNull( st.read(id) ) { "Game not found" }
    check(board is BoardRun && board.moves.size <= 5) { "Game is not available" }
    val player = if (board.moves.size > 4) board.turn else board.turn.other()
    return MultiGame(board, player ,id, target = false)
}

/**
 * @return the game with the board printed.
 */
fun Game.showBoard():Game = this
