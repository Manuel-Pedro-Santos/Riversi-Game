package pt.isel.tds.reversi.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.isel.tds.reversi.model.BoardStorage

/**
 * The principal Composable function of the application.
 * It is responsible for the creation of the application's UI.
 * The content of the application window.
 * @param onExit the function to be called when the application is closed.
 */
@Composable
fun FrameWindowScope.ReversiApp(onExit: () -> Unit,storage: BoardStorage) {
    val scope = rememberCoroutineScope()
    val vm = remember { ViewModel(scope,storage) }
    ReversiMenu(vm, onExit)
    ReversiDialog(vm)
    Column {
        LocationX()
        Row {
            LocationY()
            Box(Modifier.border(1.dp, Color.Black)) {
                BoardView(vm, vm.game?.board, onClick = vm::play)
            }
        }
        StatusBar(vm.status, vm)
    }
}




