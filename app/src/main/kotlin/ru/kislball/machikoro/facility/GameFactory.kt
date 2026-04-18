package ru.kislball.machikoro.facility

import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.triggers.special.SightsCollectedTrigger

object GameFactory {
  fun createDriver(catalog: CardCatalog, playerNames: List<String>): GameDriver {
    require(playerNames.isNotEmpty()) { "Player list must not be empty" }
    require(playerNames.all { it.isNotBlank() }) { "Player name must not be blank" }
    require(playerNames.toSet().size == playerNames.size) { "Player names must be unique" }

    val players = playerNames.map { Player(it) }
    return GameDriver(Game(catalog, players, SightsCollectedTrigger()))
  }
}
