package ru.kislball.machikoro.facility.json

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ru.kislball.machikoro.facility.GameExporter
import ru.kislball.machikoro.facility.payload.GamePayload
import ru.kislball.machikoro.facility.payload.GamePayloadMetadata
import ru.kislball.machikoro.facility.payload.PlayerPayload
import ru.kislball.machikoro.game.Game

class JSONExporter(
    private val catalogId: String? = null,
) : GameExporter {
  private val mapper = jacksonObjectMapper()

  override fun export(game: Game): String {
    val payload =
        GamePayload(
            players =
                game.players.map { player ->
                  PlayerPayload(
                      name = player.name,
                      balance = player.balance,
                      cards = player.cards.map { it.cardId },
                  )
                },
            metadata = GamePayloadMetadata(winner = game.winner?.name, catalogId = catalogId),
        )
    return mapper.writeValueAsString(
        payload.copy(metadata = payload.metadata?.copy(finished = game.finished)))
  }
}
