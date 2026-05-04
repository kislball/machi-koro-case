package ru.kislball.machikoro.storage.json

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
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.storage.GameStorage
import ru.kislball.machikoro.storage.SavedGameSummary
import ru.kislball.machikoro.storage.StoredGame

class JsonGameStorage(
    root: String,
    defaultCatalogId: String,
    catalogResolver: CardCatalogResolver,
    private val importer: JSONImporter,
    private val exporter: JSONExporter,
    private val fileExtension: String,
) : GameStorage(defaultCatalogId, catalogResolver) {
  private val root: Path = Path.of(root)

  init {
    this.root.createDirectories()
  }

  override fun save(name: String, game: GameDriver, catalogId: String) {
    fileFor(name).writeText(exporter.export(payloadFor(game, catalogId)))
  }

  override fun load(name: String): StoredGame {
    val path = fileFor(name)
    if (!path.exists()) {
      throw SaveNotFoundException(name)
    }
    return toStoredGame(importer.import(path.readText()))
  }

  override fun list(): List<SavedGameSummary> {
    return root.listDirectoryEntries("*.$fileExtension").map(::toSummary).sortedBy { it.name }
  }

  override fun delete(name: String) {
    val path = fileFor(name)
    if (!path.deleteIfExists()) {
      throw SaveNotFoundException(name)
    }
  }

  private fun toSummary(path: Path): SavedGameSummary {
    val payload = importer.import(path.readText())
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
