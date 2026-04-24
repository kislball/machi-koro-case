package ru.kislball.machikoro.storage

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.time.Instant
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText
import kotlin.io.path.writeText
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.facility.json.JSONExporter
import ru.kislball.machikoro.facility.json.JSONImporter

data class TopEntry(val playerName: String, val wins: Int)

data class StoredGame(
    val driver: GameDriver,
    val catalogId: String,
)

data class SavedGameSummary(
    val name: String,
    val playerNames: List<String>,
    val createdAt: Instant,
    val finished: Boolean,
    val winnerName: String?,
    val catalogId: String,
)

class SaveNotFoundException(val saveName: String) :
    NoSuchElementException("Saved game not found: $saveName")

class InvalidSaveNameException : IllegalArgumentException("Save name must not be blank")

class UnknownCatalogException(val catalogId: String) :
    IllegalArgumentException("Unknown catalog: $catalogId")

class GameStorage(
    private val root: Path,
    private val defaultCatalogId: String,
    private val catalogResolver: (String) -> CardCatalog?,
) {
  init {
    root.createDirectories()
  }

  fun save(name: String, game: GameDriver, catalogId: String) {
    fileFor(name).writeText(JSONExporter(catalogId).export(game.game))
  }

  fun load(name: String): StoredGame {
    val path = fileFor(name)
    if (!path.exists()) {
      throw SaveNotFoundException(name)
    }
    val content = path.readText()
    val payload = JSONImporter.parsePayload(content)
    val catalogId = payload.metadata?.catalogId ?: defaultCatalogId
    val catalog = catalogResolver(catalogId) ?: throw UnknownCatalogException(catalogId)
    return StoredGame(GameDriver(JSONImporter(catalog).import(content)), catalogId)
  }

  fun list(): List<SavedGameSummary> {
    return root.listDirectoryEntries("*.json").map(::toSummary).sortedBy { it.name }
  }

  fun delete(name: String) {
    val path = fileFor(name)
    if (!path.deleteIfExists()) {
      throw SaveNotFoundException(name)
    }
  }

  fun top(): List<TopEntry> {
    val wins = mutableMapOf<String, Int>()
    list().forEach { save ->
      val winner = save.winnerName ?: return@forEach
      wins[winner] = (wins[winner] ?: 0) + 1
    }
    return wins.entries
        .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
        .map { TopEntry(it.key, it.value) }
  }

  private fun toSummary(path: Path): SavedGameSummary {
    val content = path.readText()
    val payload = JSONImporter.parsePayload(content)
    val metadata = payload.metadata
    return SavedGameSummary(
        name = path.nameWithoutExtension,
        playerNames = payload.players.map { it.name },
        createdAt =
            Files.readAttributes(path, BasicFileAttributes::class.java).creationTime().toInstant(),
        finished = metadata?.finished ?: (metadata?.winner != null),
        winnerName = metadata?.winner,
        catalogId = metadata?.catalogId ?: defaultCatalogId,
    )
  }

  private fun fileFor(name: String): Path {
    if (name.isBlank()) {
      throw InvalidSaveNameException()
    }
    return root.resolve("$name.json")
  }
}
