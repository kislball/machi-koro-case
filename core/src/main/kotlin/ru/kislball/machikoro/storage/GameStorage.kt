package ru.kislball.machikoro.storage

import ru.kislball.machikoro.cards.common.CardCatalogResolver
import ru.kislball.machikoro.exceptions.UnknownCatalogException
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.facility.GameFactory
import ru.kislball.machikoro.facility.payload.GamePayload
import ru.kislball.machikoro.facility.payload.GamePayloadMetadata

abstract class GameStorage(
    protected val defaultCatalogId: String,
    protected val catalogResolver: CardCatalogResolver,
) {
  abstract fun save(name: String, game: GameDriver, catalogId: String)

  abstract fun load(name: String): StoredGame

  abstract fun list(): List<SavedGameSummary>

  abstract fun delete(name: String)

  open fun top(): List<TopEntry> {
    val wins = mutableMapOf<String, Int>()
    list().forEach { save ->
      val winner = save.winnerName ?: return@forEach
      wins[winner] = (wins[winner] ?: 0) + 1
    }
    return wins.entries
        .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
        .map { TopEntry(it.key, it.value) }
  }

  protected fun payloadFor(game: GameDriver, catalogId: String): GamePayload {
    val payload = GamePayload(game.game)
    return payload.copy(
        metadata = (payload.metadata ?: GamePayloadMetadata()).copy(catalogId = catalogId))
  }

  protected fun toStoredGame(payload: GamePayload): StoredGame {
    val catalogId = payload.catalogId ?: defaultCatalogId
    if (catalogResolver[catalogId] == null) {
      throw UnknownCatalogException(catalogId)
    }
    payload.catalogResolver = catalogResolver
    return StoredGame(GameFactory.createDriver(payload), catalogId)
  }
}
