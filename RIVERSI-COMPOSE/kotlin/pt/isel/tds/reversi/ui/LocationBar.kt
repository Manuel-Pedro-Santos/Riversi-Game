package pt.isel.tds.reversi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isel.tds.reversi.model.*

val sizeLocation = 15.dp


/**
 * Bar for the letter on the board
 */

@Composable
fun LocationX() {
    val mod = Modifier.width(boardSize + sizeLocation).background(Color.DarkGray).height(sizeLocation)
    Row(modifier = mod) {
        Box(modifier = Modifier.size(sizeLocation))
        for (x in 1..BOARD_DIM){
            val letter = ('A' + x - 1).toString()
            Text(letter, color = Color.White, fontSize = 15.sp,modifier = Modifier.width(cellSize), textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(lineSize).background(Color.White))

        }

    }
}

/**
* Bar for the number in the board
* */
@Composable
fun LocationY(){
    val mod = Modifier.height(boardSize).background(Color.DarkGray).width(sizeLocation)
    Column (modifier = mod){
        for (y in 1..BOARD_DIM) {
            Box(
                modifier = Modifier.height(cellSize).width(sizeLocation),
                contentAlignment = Alignment.Center
            ){
                Text("$y", color = Color.White,  fontSize = 15.sp, textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(lineSize))
        }
    }
}
