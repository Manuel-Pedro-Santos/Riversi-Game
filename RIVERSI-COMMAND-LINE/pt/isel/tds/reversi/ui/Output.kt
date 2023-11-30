package pt.isel.tds.reversi.ui
import pt.isel.tds.reversi.model.*

/**
 * Shows the board of the game and the current turn.
 * The board is shown as a grid in format:
 *   A B C D E F G H
 * 1 . . . . . . . .
 * 2 . . . . . . . .
 * 3 . . . . . . . .
 * 4 . . . @ # . . .
 * 5 . . . # @ . . .
 * 6 . . . . . . . .
 * 7 . . . . . . . .
 * 8 . . . . . . . .
 * # = 2 | @ = 2
 * turn: WHITE
 * Where the dots represent empty cells, the # represents the number of black pieces and the @ the number of white pieces.
 * @receiver the game to show.
 */

fun Game.show() {
    if (board is BoardRun) {
        val validPlays = board.playableCells()
        if(this is MultiGame) println("You are player ${ player.type } in game $id")
        print("  ")
        Cell.values.filter { it.rowIndex == 0 }.forEach { cell ->
            print(" ${'A' + cell.colIndex}")
        }
        println()
        Cell.values.forEach { cell ->
            if (cell.colIndex == 0) print("${cell.rowIndex + 1} ")
            when(this){
                is MultiGame -> {
                    if(board.turn != player) print(" ${board.moves[cell]?.type ?: '.'}")
                    else if(target) print(" ${board.moves[cell]?.type ?: if(cell in validPlays ) '*' else '.'}")
                    else print(" ${board.moves[cell]?.type ?: '.'}")
                }
                is SingleGame -> if(target)print(" ${board.moves[cell]?.type ?: if(cell in validPlays ) '*' else '.'}")
                else print(" ${board.moves[cell]?.type ?: '.'}")
            }
            if (cell.colIndex == BOARD_DIM - 1) println()
        }
    }
    println("# = ${board.moves.count { it.value == Player.BLACK }} | @ = ${board.moves.count { it.value == Player.WHITE }}")
    println( when(board) {
        is BoardPass -> "turn: ${board.turn.type}"
        is BoardRun-> "turn: ${board.turn.type}"
        is BoardWin -> "winner: ${board.winner.type}"
        is BoardDraw -> "Draw"
    } )
}


