package ru.kislball.machikoro.facility

import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player

object GameFactory {
    private val jsonExporter: GameExporter = JSONExporter()
    private val jsonImporter: GameImporter = JSONImporter()

    fun createDriver(playerNames: List<String>): GameDriver {
        require(playerNames.isNotEmpty()) { "Player list must not be empty" }
        require(playerNames.all { it.isNotBlank() }) { "Player name must not be blank" }
        require(playerNames.toSet().size == playerNames.size) { "Player names must be unique" }

        val players = playerNames.map(::Player)
        return GameDriver(Game(players))
    }

    fun export(game: Game, exporter: GameExporter = jsonExporter): String {
        return exporter.export(game)
    }

    fun import(content: String, importer: GameImporter = jsonImporter): GameDriver {
        return GameDriver(importer.import(content))
    }
}
