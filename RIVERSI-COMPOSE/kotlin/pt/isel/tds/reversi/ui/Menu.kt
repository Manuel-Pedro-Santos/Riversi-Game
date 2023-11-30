package pt.isel.tds.reversi.ui
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.*
import pt.isel.tds.reversi.model.isNew
import java.awt.MenuBar
/**
 * Menu of the application.
 */
@Composable
fun FrameWindowScope.ReversiMenu(vm:ViewModel, onExit: ()->Unit) = MenuBar {
    Menu("Game"){
        Item("New", enabled = !vm.isNewGame(),onClick = { vm.openDialog(Dialog.NEW) })
        Item("Join", onClick = {vm.openDialog(Dialog.JOIN)})
        Item("Refresh", enabled = vm.canRefresh, onClick = {vm.refresh()})
        Item("Exit", onClick = onExit)
    }
    Menu("Play"){
        Item("Pass", enabled = vm.isGamePass(),onClick =vm::pass)
    }
    Menu("Option"){
        CheckboxItem("Show targets",checked = vm.targetsFlag, onCheckedChange = { vm.targets() })
        CheckboxItem("Auto-refresh", checked = vm.autoRefresh, onCheckedChange = { vm.toggleAutoRefresh() })

    }
}

