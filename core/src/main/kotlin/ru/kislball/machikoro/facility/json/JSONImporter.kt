package ru.kislball.machikoro.facility.json

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.kislball.machikoro.facility.GameImporter
import ru.kislball.machikoro.facility.payload.GamePayload
import ru.kislball.machikoro.facility.payload.GamePayloadMetadata
import ru.kislball.machikoro.facility.payload.PlayerPayload

class JSONImporter : GameImporter {
  private val mapper = jacksonObjectMapper()

  override fun import(s: String): GamePayload {
    return try {
      mapper.readValue<JsonImportGamePayload>(s).toPayload()
    } catch (exception: JsonProcessingException) {
      throw IllegalArgumentException("Invalid JSON content", exception)
    }
  }

  companion object {
    fun parsePayload(content: String): GamePayload {
      return JSONImporter().import(content)
    }

    fun parseCatalogId(content: String): String? {
      return parsePayload(content).catalogId
    }
  }
}

private data class JsonImportGamePayload(
    val players: List<PlayerPayload>,
    val metadata: JsonGamePayloadMetadata? = null,
    @param:JsonProperty("catalogId") private val topLevelCatalogId: String? = null,
) {
  val catalogId: String?
    get() = topLevelCatalogId ?: metadata?.catalogId

  fun toPayload(): GamePayload {
    return GamePayload(
        players = players,
        metadata =
            metadata?.let {
              GamePayloadMetadata(
                  winner = it.winner,
                  catalogId = catalogId,
                  finished = it.finished,
              )
            },
    )
  }
}

private data class JsonGamePayloadMetadata(
    val winner: String? = null,
    val finished: Boolean? = null,
    val catalogId: String? = null,
)
