package ru.kislball.machikoro.facility

import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.exceptions.EmptyPlayersListException
import ru.kislball.machikoro.exceptions.PlayerNameBlankException
import ru.kislball.machikoro.exceptions.PlayerNamesNotUniqueException
import ru.kislball.machikoro.exceptions.require
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.triggers.special.SightsCollectedTrigger

object GameFactory {
  fun createDriver(catalog: CardCatalog, playerNames: List<String>): GameDriver {
    require(playerNames.isNotEmpty()) { EmptyPlayersListException() }
    require(playerNames.all { it.isNotBlank() }) { PlayerNameBlankException() }
    require(playerNames.toSet().size == playerNames.size) { PlayerNamesNotUniqueException() }

    val players = playerNames.map { Player(it) }
    return GameDriver(Game(catalog, players, SightsCollectedTrigger()))
  }
}
