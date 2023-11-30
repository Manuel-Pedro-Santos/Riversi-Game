import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import pt.isel.tds.reversi.model.BoardSerializer
import pt.isel.tds.reversi.model.BoardStorage
import pt.isel.tds.reversi.storage.MongoDriver
import pt.isel.tds.reversi.storage.MongoStorage
import pt.isel.tds.reversi.ui.CellView
import pt.isel.tds.reversi.ui.ReversiApp
import pt.isel.tds.reversi.ui.cellSize

@Preview
fun main() {
    MongoDriver().use { driver ->
        val storage: BoardStorage = MongoStorage("games", driver, BoardSerializer)
        application(exitProcessOnExit = false) {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Reversi GRUPO 5",
                state = WindowState(size = DpSize.Unspecified)
            ) {
                ReversiApp(::exitApplication,storage)
            }
        }
    }
}
