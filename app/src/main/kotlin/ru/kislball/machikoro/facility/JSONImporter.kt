package ru.kislball.machikoro.facility

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.kislball.machikoro.cards.CardFactory
import ru.kislball.machikoro.cards.CardKind
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player

class JSONImporter : GameImporter {
  private val mapper = jacksonObjectMapper()

  override fun import(content: String): Game {
    return try {
      val parsed: GameJson = mapper.readValue(content)
      val players = parsed.players.map(::parsePlayer)
      Game(players)
    } catch (exception: Exception) {
      throw IllegalArgumentException("Invalid JSON content", exception)
    }
  }

  private fun parsePlayer(rawPlayer: PlayerJson): Player {
    val player = Player(rawPlayer.name)
    player.balance = rawPlayer.balance
    player.cards.addAll(rawPlayer.cards.map(::parseCard))
    return player
  }

  private fun parseCard(kindName: String) =
      try {
        CardFactory.create(CardKind.valueOf(kindName))
      } catch (exception: IllegalArgumentException) {
        throw IllegalArgumentException("Invalid card kind: $kindName", exception)
      }
}
