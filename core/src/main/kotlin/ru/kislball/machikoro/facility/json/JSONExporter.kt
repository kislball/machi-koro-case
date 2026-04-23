package ru.kislball.machikoro.facility.json

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ru.kislball.machikoro.facility.GameExporter
import ru.kislball.machikoro.game.Game

class JSONExporter : GameExporter {
  private val mapper = jacksonObjectMapper()

  override fun export(game: Game): String {
    val payload =
        GameJson(
            players =
                game.players.map { player ->
                  PlayerJson(
                      name = player.name,
                      balance = player.balance,
                      cards = player.cards.map { it.cardId },
                  )
                },
            metadata = GameMetadataJson(winner = game.winner?.name),
        )
    return mapper.writeValueAsString(payload)
  }
}
