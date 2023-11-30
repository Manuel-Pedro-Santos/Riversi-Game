package pt.isel.tds.reversi.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.WindowPosition
import kotlinx.coroutines.delay
import pt.isel.tds.reversi.model.BoardStorage
import pt.isel.tds.reversi.model.Player



val NewDiaWordWidth = 300.dp
val JoinDialogWidth = 250.dp

@Composable
fun ReversiDialog(vm: ViewModel): Unit =
    when(vm.open) {
        Dialog.NEW -> NewGameDialog(vm::closeDialog, vm::newGame)
        Dialog.JOIN -> JoinGameDialog(vm::closeDialog, vm::joinGame)
        Dialog.MESSAGE -> MessageDialog(vm::closeDialog, vm.message)
        null -> Unit
    }



@Composable
fun NewGameDialog(onClose: () -> Unit, onNewGame: (String,Player,Boolean) -> Unit) {
    var multiplayerChecked by remember { mutableStateOf(true) }
    var currentPlayer by  remember { mutableStateOf(Player.WHITE) }
    var label by remember { mutableStateOf("") }

    Dialog(
        onCloseRequest = onClose,
        state = DialogState(size = DpSize.Unspecified, position = WindowPosition.Aligned(Alignment.Center)),
        title = "New Game"
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.width(NewDiaWordWidth)
        ) {
            Row {
                Checkbox(
                    checked = multiplayerChecked,
                    onCheckedChange = { multiplayerChecked = it }
                )
                Text("Multiplayer", modifier = Modifier.align(Alignment.CenterVertically))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (multiplayerChecked) {
                    // Multiplayer content
                    Text("Game name:", modifier = Modifier.align(Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(10.dp))
                    TextField(
                        value = label,
                        onValueChange = { label = it },
                        modifier = Modifier.width(NewDiaWordWidth * 0.6f)
                    )
                }
            }
            Box(
                modifier = Modifier.clickable { currentPlayer = currentPlayer.other() }
                    .background(color = Color.White)
            ) {
                PlayerView("Player:", currentPlayer)
            }

            Button(onClick ={ onNewGame(label,currentPlayer,multiplayerChecked ) },
                enabled = if(multiplayerChecked )label.isNotBlank()else true) {
                Text("New Game")
            }
        }
    }
}

@Composable
fun JoinGameDialog(onClose: () -> Unit, onJoinGame: (String) -> Unit) {
    var textFieldValue by remember { mutableStateOf("") }
    Dialog(
        onCloseRequest = onClose,
        state = DialogState(size = DpSize.Unspecified, position = WindowPosition.Aligned(Alignment.Center)),
        title = "Join Game"
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.width(JoinDialogWidth)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                // Multiplayer content
                Text("Game name:", modifier = Modifier.align(Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(10.dp))
                TextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    modifier = Modifier.width(JoinDialogWidth * 0.6f)
                )
            }
            Row {
                Button(onClick = {onJoinGame(textFieldValue)}, enabled = textFieldValue.isNotBlank(), ) {
                    Text("Join Game")
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun MessageDialog(onClose: () -> Unit, message: String) =
    Dialog(
        onCloseRequest = onClose,
        state = DialogState(size = DpSize.Unspecified, position = WindowPosition(Alignment.Center)),
        title = "ERROR"
    ) {
        Box(
            modifier = Modifier
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                message,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Default,
                    color = Color.Black
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }


