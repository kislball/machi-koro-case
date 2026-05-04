package ru.kislball.machikoro.facility

import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.exceptions.EmptyPlayersListException
import ru.kislball.machikoro.exceptions.PlayerNameBlankException
import ru.kislball.machikoro.exceptions.PlayerNamesNotUniqueException
import ru.kislball.machikoro.exceptions.require
import ru.kislball.machikoro.facility.payload.GamePayload
import ru.kislball.machikoro.facility.payload.PlayerPayload
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.triggers.special.SightsCollectedTrigger

object GameFactory {
  fun createDriver(
      catalog: CardCatalog,
      playerNames: List<String>,
      initialBalance: Int = 3
  ): GameDriver {
    require(playerNames.isNotEmpty()) { EmptyPlayersListException() }
    require(playerNames.all { it.isNotBlank() }) { PlayerNameBlankException() }
    require(playerNames.toSet().size == playerNames.size) { PlayerNamesNotUniqueException() }

    val players =
        playerNames.map {
          Player(it).apply {
            balance = initialBalance
            cards = catalog.getStarterCards().toMutableList()
          }
        }
    return GameDriver(Game(catalog, players, SightsCollectedTrigger()))
  }

  fun createDriver(payload: GamePayload): GameDriver {
    val players = payload.players.map { it.toPlayer(payload.catalog) }
    val winner =
        payload.metadata?.winner?.let { winnerName ->
          players.firstOrNull { it.name == winnerName }
              ?: throw IllegalArgumentException("Invalid winner: $winnerName")
        }
    val finished = payload.metadata?.finished ?: (winner != null)
    return GameDriver(Game(payload.catalog, players, SightsCollectedTrigger(), winner, finished))
  }

  private fun PlayerPayload.toPlayer(catalog: CardCatalog): Player {
    val player = Player(name)
    player.balance = balance
    player.cards.addAll(
        cards.map { cardId ->
          catalog[cardId] ?: throw IllegalArgumentException("Invalid card kind: $cardId")
        })
    return player
  }
}
