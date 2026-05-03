package ru.kislball.machikoro.facility.json

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ru.kislball.machikoro.facility.GameExporter
import ru.kislball.machikoro.facility.payload.GamePayload
import ru.kislball.machikoro.facility.payload.GamePayloadMetadata
import ru.kislball.machikoro.facility.payload.PlayerPayload

class JSONExporter(
    private val catalogId: String? = null,
) : GameExporter {
  private val mapper = jacksonObjectMapper()

  override fun export(g: GamePayload): String {
    return mapper.writeValueAsString(
        JsonExportGamePayload(
            players = g.players,
            metadata =
                (g.metadata ?: GamePayloadMetadata()).copy(catalogId = catalogId ?: g.catalogId),
        ))
  }
}

private data class JsonExportGamePayload(
    val players: List<PlayerPayload>,
    val metadata: GamePayloadMetadata? = null,
)
