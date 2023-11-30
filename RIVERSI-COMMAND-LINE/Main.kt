
import pt.isel.tds.reversi.model.*
import pt.isel.tds.reversi.storage.MongoDriver
import pt.isel.tds.reversi.storage.MongoStorage
import pt.isel.tds.reversi.ui.*
import pt.isel.tds.reversi.storage.TextFileStorage

fun main() {
    var game: Game? = null
    val driver = MongoDriver()
    val commands = getCommands(MongoStorage("games", driver, BoardSerializer))
    //val commands = getCommands(TextFileStorage("games", BoardSerializer))
    while (true) {
        val (name, args) = readCommand()
        val cmd = commands[name]
        if (cmd==null) println("Invalid command $name")
        else try {
            game = cmd.execute(args, game)
            if (cmd.isToFinish()) break
            game?.show()
        } catch (e: Exception) {
            println(e.message)
            if (e is IllegalArgumentException)
                println("Use: $name ${cmd.argsSyntax}")
        }
    }
    println("Bye.")
    driver.close()
}
