package pt.isel.tds.reversi.ui

import pt.isel.tds.reversi.storage.*
import pt.isel.tds.reversi.model.*

/**
 * Base class for all commands.
 */
class Command(
    val argsSyntax: String = "",
    val isToFinish: () -> Boolean = { false },
    val execute: (List<String>, Game?) -> Game? = { _, g -> g }
)



/**
 * Returns a map of all commands supported by the application.
 */
fun getCommands(st: BoardStorage) = mapOf(
    "PLAY" to Command("<cell>") { args, game ->
        require(args.isNotEmpty()) { "Missing argument" }
        val pos = requireNotNull(args.first().uppercase().toCellOrNull()) { "Invalid position ${args.first()}" }
        checkNotNull(game) { "Game not started" }
        game.play(pos,st)
    },
    "NEW" to Command("(#|@) [<name>]") { args, _ ->
        require(args.isNotEmpty()) { "Missing game" }
        val player = requireNotNull(args.first().uppercase().first().toPlayer()) { "Invalid player ${args.first()}" }
        if (args.size == 2) createMultiGame(args[1],player,st) else createSingleGame(player)
    },
    "JOIN" to Command("<game>"){ args, _ ->
        require(args.isNotEmpty()) { "Missing game" }
        joinGame(args[0], st)
    },
    "REFRESH" to Command{ _, game ->
        checkNotNull(game) { "Game not started" }
        game.refresh(st)
    },
    "SHOW" to Command{ _, game ->
        checkNotNull(game) { "Game not started" }
        game.showBoard()
    },
    "TARGETS" to Command("[ON/OFF]"){ args, game ->
        checkNotNull(game) { "Game not started" }
        checkNotNull(args.isNotEmpty()) { "Missing argument" }
        val mode = args[0].uppercase()
        game.targets(mode, st)
    },
    "PASS" to Command { _, game ->
        checkNotNull(game) { "Game not started" }
        game.pass(st)
    },
    "EXIT" to Command(isToFinish = { true })
)

