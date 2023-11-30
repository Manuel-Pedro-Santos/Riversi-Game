package pt.isel.tds.reversi.model


const val BOARD_DIM =8
const val MAX_MOVES = BOARD_DIM * BOARD_DIM

enum class Player { BLACK, WHITE;
    fun other() = if (this==BLACK) WHITE else BLACK
}


// Type to represents all the moves in the game.
typealias Moves = Map<Cell,Player>

/**
 * Represents the board of the game.
 * Store all [moves] in a map from [Cell] to [Player] ([Moves]).
 * There are four possible states of board: [BoardRun], [BoardWin] , [BoardDraw] and [BoardPass].
 * These hierarchy is to be used by pattern matching.
 * [BoardPass] returns a new board [BoardRun] with the same [moves] and the [turn] changed.
 */
sealed class Board(val moves: Moves){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Board) return false
        if (this::class != other::class) return false
        return moves.size == other.moves.size
    }
    override fun hashCode(): Int = moves.hashCode()
}
open class BoardRun(moves: Moves, val turn: Player): Board(moves)
class BoardWin(moves: Moves, val winner: Player): Board(moves)
class BoardDraw(moves: Moves) : Board(moves)
class BoardPass(moves: Moves,  turn: Player): BoardRun(moves, turn)



/**
 * Creates a new board with the given [first] as the first turn.
 **/
fun createBoard(first: Player):Board = BoardRun(moves =mapOf(
    Cell(BOARD_DIM / 2 - 1, BOARD_DIM / 2 - 1) to Player.WHITE,
    Cell(BOARD_DIM / 2, BOARD_DIM / 2 - 1) to Player.BLACK,
    Cell(BOARD_DIM / 2 - 1, BOARD_DIM / 2) to Player.BLACK,
    Cell(BOARD_DIM / 2, BOARD_DIM / 2) to Player.WHITE,
), first)

/**
 * Makes a move in [pos] position by the current turn.
 * @throws IllegalArgumentException if the [pos] is already used.
 * @throws IllegalStateException if the game is over (Draw or Win).
 * Proceeds to check if the play is a winning play or a draw play.
 */
fun Board.play(cell: Cell): Board = when(this) {
    is BoardRun -> {
        check(moves[cell] == null) { "Position $cell is not empty" }
        check(playableCell(cell)) { "Position $cell is not playable" }
        val moves = cellsToChange(cell) + (cell to turn)
        when {
            isWin(moves) -> BoardWin(moves, winner(moves))
            moves.size == MAX_MOVES-> BoardDraw(moves)
            else -> BoardRun(moves, turn.other())
        }
    }
    else ->  error("Game is over")
}

/**
 * @throws IllegalArgumentException if there is a [pos] that can be played.
 * If the Board is a [BoardRun] and there is no playable cells, then it is a [BoardPass].
 * If the Board is a [BoardPass] and there is no playable cells, then it is a [BoardWin] or [BoardDraw].
 */
fun Board.pass(): Board {
    val cells = playableCells()
    println("passing")
    check(cells.isEmpty()) { "Play at least ${cells.first()}" }
    return when (this) {
        is BoardPass -> isWin()
        is BoardRun -> BoardPass(moves, turn.other())
        else ->  error("Game is over")
    }
}
/**
 * @return a list of all playable cells.
 */
private fun BoardRun.isWin(moves: Moves) =
    moves.values.count { it == turn }  > moves.values.count { it == turn.other() } &&
            moves.keys.size == MAX_MOVES
            ||
            moves.values.count { it == turn } < moves.values.count { it == turn.other() } &&
            moves.keys.size == MAX_MOVES


private fun BoardRun.winner(moves: Moves) =
    if (moves.values.count { it == turn } > moves.values.count { it == turn.other() }) turn
    else turn.other()

/**
 * @return a [BoardWin] if the current player has more pieces than the other player or a [BoardDraw] if the number of pieces is equal.
 */
private fun BoardPass.isWin() : Board {
    return if(moves.values.count{it == turn } > moves.values.count { it == turn.other() } || moves.values.count { it == turn.other()} ==0) BoardWin(moves, turn)
    else if(moves.values.count{it == turn } < moves.values.count { it == turn.other() } || moves.values.count { it == turn} ==0) BoardWin(moves, turn.other())
    else BoardDraw(moves)
}

/**
 * @throws IllegalArgumentException if the [pos] is not in the board.
 * @return a Boolean that indicates if the [pos] is playable.
 */
fun Board.playableCell(pos: Cell): Boolean {
    if( this is BoardRun) {
        if (pos in moves.keys) return false
        val neighbors = Direction.values().map { dir -> pos + dir }.filter { moves[it] == turn.other() || moves[it] == turn } //get all neighbors
        if (!neighbors.any { moves[it] == turn.other() }) return false
        Direction.values().forEach { direction ->
            val cellsInFront = cellsInDirection(pos, direction) //get all cells in direction
            val c = cellsInFront.takeWhile { it in moves.keys && moves[it] != turn }
            if (c.isNotEmpty() && moves[c.last() + direction] == turn) return true
        }
    }
    return false
}

/**
 * @throws IllegalArgumentException if the [pos] is not in the board.
 * @return a list of all cells that can be played from the given [pos].
 */
fun Board.playableCells() : List<Cell>  = Cell.values.filter { it  -> playableCell(it) }

/**
 * @return a new map with all the cells that need to be changed.
 */
fun BoardRun.cellsToChange(pos: Cell): Moves {
    val c = buildList<Cell> { Direction.values().forEach { direction ->
                val cells =  cellsInDirection(pos, direction).takeWhile { it in moves.keys && moves[it] != turn }
                if (cells.isNotEmpty() && moves[cells.last() + direction] == turn) addAll(cells)
                }
    }
    return moves.mapValues { if(it.key in c)  turn else it.value }
}

/**
 * Based on the given char [c] returns the corresponding [Player].
 */
fun Char.toPlayer() = when(this) {
    '@' -> Player.WHITE
    '#' -> Player.BLACK
    else -> null

}


val Board.isNew get()=  this.moves.size == 4

val Board.isPass get() = playableCells().isEmpty()
val Board.isWinner get() = this is BoardWin
val Board.isDraw get() = this is BoardDraw
