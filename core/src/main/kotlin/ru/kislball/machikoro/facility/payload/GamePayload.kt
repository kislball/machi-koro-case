package ru.kislball.machikoro.facility.payload

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.common.CardCatalogResolver
import ru.kislball.machikoro.cards.common.catalogId
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.exceptions.UnknownCatalogException
import ru.kislball.machikoro.game.Game

data class GamePayload(
    val players: List<PlayerPayload>,
    val metadata: GamePayloadMetadata? = null,
) {
  @get:JsonIgnore var catalogResolver: CardCatalogResolver = CardCatalogResolver.default

  @get:JsonIgnore
  val catalog: CardCatalog
    get() {
      val id = catalogId ?: StandardCatalog.catalogId
      return catalogResolver[id] ?: throw UnknownCatalogException(id)
    }

  @get:JsonIgnore
  val catalogId: String?
    get() = metadata?.catalogId

  constructor(
      g: Game
  ) : this(
      players =
          g.players.map { player ->
            PlayerPayload(
                name = player.name,
                balance = player.balance,
                cards = player.cards.map { it.cardId },
            )
          },
      metadata =
          GamePayloadMetadata(
              winner = g.winner?.name,
              catalogId = catalogIdOf(g),
              finished = g.finished,
          ),
  )

  companion object {
    private fun catalogIdOf(g: Game): String? = runCatching { g.catalog.catalogId }.getOrNull()
  }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class GamePayloadMetadata(
    val winner: String? = null,
    val catalogId: String? = null,
    val finished: Boolean? = null,
)

data class PlayerPayload(val name: String, val balance: Int, val cards: List<String>)
