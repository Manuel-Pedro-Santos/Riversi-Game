package pt.isel.tds.reversi.ui

import pt.isel.tds.reversi.model.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val statusBarHeight = 30.dp
val statusBarWidth = boardSize + sizeLocation

/**
 * The Composable function for of player with one label.
 * @param label the label for the player.
 * @param player the player to be presented.
 * @param animated if true the player is animated.
 */
@Composable
fun PlayerView(label:String, player: Player, animated:Boolean = true,modifier: Modifier = Modifier) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically,
) {
    Text(label,fontSize = 20.sp)
    Spacer(modifier = Modifier.width(3.dp))
    CellView(player, Modifier.size(28.dp), animated = animated)
}

/**
 * The Composable function to present the points for a player .
 * @param player the player to be presented.
 * @param animated if true the player is animated.
 *
 */

@Composable
fun PointsView(vm: ViewModel, player: Player, animated: Boolean = false, modifier: Modifier = Modifier) {
    val points = vm.getPoints(player)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Text("$points", style = TextStyle(fontWeight = FontWeight.Bold), fontSize = 28.sp, textAlign = TextAlign.Center)
        Text("x", fontSize = 17.sp)
        Spacer(modifier = Modifier.width(3.dp))
        CellView(player, Modifier.size(28.dp), animated = animated)
    }
}


/**
 * The Composable function responsible for the presentation of the status bar.
 */
@Composable
fun StatusBar(info: StatusInfo, vm:ViewModel) {
    val mod = Modifier.width(statusBarWidth).height(statusBarHeight).background(Color.LightGray)
    val (label, player) = info
    if (player == null)
        Text(
            text = label,
            modifier = mod,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                color = Color.Black
            )
        )
    else if (label == "Winner")
        statusBarDisplay(vm, label, player, mod)
    else
        if (vm.board.moves.isNotEmpty())
            statusBarDisplay(vm, label, player, mod)
}

@Composable
fun statusBarDisplay(vm:ViewModel,label: String,actualPlayer: Player,mod: Modifier ) {
    Row(modifier = mod.fillMaxWidth()) {
        Box {
            PlayerView(
                label,
                actualPlayer,
                animated = true,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
            )
        }
        if (vm.isMultiGame) {
            Box(modifier = Modifier.width((statusBarWidth / 4))) {
                PlayerView(
                    "You",
                    (vm.game as MultiGame).player,
                    animated = false,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        Box {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PointsView(vm, Player.WHITE, animated = false)
                PointsView(vm, Player.BLACK, animated = false)
            }
        }
    }
}
