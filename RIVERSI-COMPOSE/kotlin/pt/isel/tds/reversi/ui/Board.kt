package pt.isel.tds.reversi.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import pt.isel.tds.reversi.model.*
import pt.isel.tds.view.Sprites

// Dimensions of the board presentation.
val cellSize = 65.dp
val lineSize = 2.dp
val boardSize = cellSize * BOARD_DIM + lineSize*(BOARD_DIM -1)


/**
 * The Composable function responsible for the presentation of the board.
 * @param board the board to be presented.
 * @param onClick the function to be called when a cell is clicked.
 */

@Composable
fun BoardView(vm:ViewModel,board: Board?, onClick: (Cell) -> Unit) {
    if (board == null) {
        Column(
            modifier = Modifier.size(boardSize).background(Color.Black),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            repeat(BOARD_DIM) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    repeat(BOARD_DIM) { col->
                        val pos = Cell(row,col)
                        CellView(null, animated = false) { onClick(pos) }
                    }
                }
            }
        }
    }
    else
        Column(
            modifier = Modifier.size(boardSize).background(Color.Black),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            repeat(BOARD_DIM) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    repeat(BOARD_DIM) { col->
                        val pos = Cell(row,col)
                        if (board.moves[pos] != null)
                            CellView(player = board.moves[pos], animated = vm.cellsRotating(pos)) { onClick(pos) }
                        else if (vm.cellsTarget(pos) && vm.isMultiGame && vm.isMyTurn() || vm.cellsTarget(pos) && !vm.isMultiGame)
                            TargetsView{ onClick(pos) }

                        else
                            CellView(null, animated = false) { onClick(pos) }
                    }
                }
            }
        }
}


/**
 * The Composable function responsible for the presentation of each cell.
 * @param player the player that played on the cell.
 * @param onClick the function to be called when the cell is clicked.
 */
@Composable
fun CellView(
    player: Player?,
    modifier: Modifier = Modifier.size(cellSize).background(Color.Green),
    animated: Boolean = true,
    onClick: () -> Unit = {}
) {
    if (player == null) {
        Box(modifier = modifier.clickable(onClick = onClick))
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            val sprites = Sprites("sprites1.png")
            val image = when (player) {
                Player.WHITE -> sprites[0, 6]
                Player.BLACK -> sprites[1, 6]
            }
            val framesSequence = listOf(
                sprites[0, 6], sprites[0, 7], sprites[1, 0], sprites[1, 1], sprites[1, 2],
                sprites[1, 3], sprites[1, 4], sprites[1, 5], sprites[1, 6]
            )
            var idx by remember(player) { mutableStateOf(if (player == Player.WHITE) 8 else 0) }
            if (animated) {
                idx = PiecesRotation(player, idx)
            }
            Image(
                bitmap = if (animated) framesSequence[idx] else image,
                modifier = Modifier.fillMaxSize(),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun PiecesRotation(player: Player?, idx: Int): Int {
    val idx1 = remember(player) { mutableStateOf(idx) }
    LaunchedEffect(player) {
        while (true) {
            delay(50)
            if (player == Player.WHITE) {
                if (idx1.value == 0)
                    break
                idx1.value--
            } else {
                if (idx1.value == 8)
                    break
                idx1.value++
            }
        }
    }
    return idx1.value
}


/**
 * The Composable function responsible for the presentation of targets.
 * @param onClick the function to be called when the cell is clicked.
 */
@Composable
fun TargetsView(onClick : () -> Unit) {
    Box(
        modifier = Modifier.size(cellSize).background(Color.Green).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(Color.Yellow, shape = CircleShape))
    }
}





