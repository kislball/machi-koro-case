package ru.kislball.machikoro.storage

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText
import kotlin.io.path.writeText
import ru.kislball.machikoro.cards.common.CardCatalogResolver
import ru.kislball.machikoro.exceptions.InvalidSaveNameException
import ru.kislball.machikoro.exceptions.SaveNotFoundException
import ru.kislball.machikoro.exceptions.UnknownCatalogException
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.facility.GameExporter
import ru.kislball.machikoro.facility.GameFactory
import ru.kislball.machikoro.facility.GameImporter
import ru.kislball.machikoro.facility.json.JSONExporter
import ru.kislball.machikoro.facility.json.JSONImporter
import ru.kislball.machikoro.facility.payload.GamePayload
import ru.kislball.machikoro.facility.payload.GamePayloadMetadata

class GameStorage(
    private val root: Path,
    private val defaultCatalogId: String,
    private val catalogResolver: CardCatalogResolver,
    private val importer: GameImporter = JSONImporter(),
    private val exporter: GameExporter = JSONExporter(),
    private val fileExtension: String = "json",
) {
  init {
    root.createDirectories()
  }

  fun save(name: String, game: GameDriver, catalogId: String) {
    val payload = GamePayload(game.game)
    val payloadWithCatalogId =
        payload.copy(
            metadata = (payload.metadata ?: GamePayloadMetadata()).copy(catalogId = catalogId))
    fileFor(name).writeText(exporter.export(payloadWithCatalogId))
  }

  fun load(name: String): StoredGame {
    val path = fileFor(name)
    if (!path.exists()) {
      throw SaveNotFoundException(name)
    }
    val content = path.readText()
    val payload = importer.import(content)
    val catalogId = payload.catalogId ?: defaultCatalogId
    if (catalogResolver[catalogId] == null) {
      throw UnknownCatalogException(catalogId)
    }
    payload.catalogResolver = catalogResolver
    return StoredGame(GameFactory.createDriver(payload), catalogId)
  }

  fun list(): List<SavedGameSummary> {
    return root.listDirectoryEntries("*.$fileExtension").map(::toSummary).sortedBy { it.name }
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
    val payload = importer.import(content)
    val metadata = payload.metadata
    return SavedGameSummary(
        name = path.nameWithoutExtension,
        playerNames = payload.players.map { it.name },
        createdAt =
            Files.readAttributes(path, BasicFileAttributes::class.java).creationTime().toInstant(),
        finished = metadata?.finished ?: (metadata?.winner != null),
        winnerName = metadata?.winner,
        catalogId = payload.catalogId ?: defaultCatalogId,
    )
  }

  private fun fileFor(name: String): Path {
    if (name.isBlank()) {
      throw InvalidSaveNameException()
    }
    return root.resolve("$name.$fileExtension")
  }
}
