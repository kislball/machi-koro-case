package ru.kislball.machikoro.facility.json

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.facility.GameImporter
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.triggers.special.SightsCollectedTrigger

class JSONImporter(val catalog: CardCatalog) : GameImporter {
  private val mapper = jacksonObjectMapper()

  override fun import(content: String): Game {
    return try {
      val parsed: GameJson = mapper.readValue(content)
      val players = parsed.players.map(::parsePlayer)
      val winner =
          parsed.metadata?.winner?.let { winnerName ->
            players.firstOrNull { it.name == winnerName }
                ?: throw IllegalArgumentException("Invalid winner: $winnerName")
          }
      Game(catalog, players, SightsCollectedTrigger(), winner)
    } catch (exception: JsonProcessingException) {
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
      catalog[kindName] ?: throw IllegalArgumentException("Invalid card kind: $kindName")

  companion object {
    private val mapper = jacksonObjectMapper()

    fun parsePayload(content: String): GameJson {
      return mapper.readValue(content)
    }
  }
}
