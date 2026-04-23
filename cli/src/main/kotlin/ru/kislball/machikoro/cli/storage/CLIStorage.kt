package ru.kislball.machikoro.cli.storage

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText
import kotlin.io.path.writeText
import ru.kislball.machikoro.cli.catalog.CLICatalogRegistry
import ru.kislball.machikoro.cli.error.CLIException
import ru.kislball.machikoro.cli.session.ActiveCliGame
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.facility.json.JSONExporter
import ru.kislball.machikoro.facility.json.GameJson
import ru.kislball.machikoro.facility.json.JSONImporter

data class TopEntry(val playerName: String, val wins: Int)

class CLIStorage(
    private val root: Path,
    private val catalogs: CLICatalogRegistry = CLICatalogRegistry.default(),
) {
  init {
    root.createDirectories()
  }

  fun save(name: String, game: ActiveCliGame) {
    fileFor(name).writeText(JSONExporter(game.catalogId).export(game.driver.game))
  }

  fun load(name: String): ActiveCliGame {
    val path = fileFor(name)
    if (!path.exists()) {
      throw CLIException("cli.games.not_found", name)
    }
    val content = path.readText()
    val catalogId = parseCatalogId(content)
    val catalog =
        catalogs.get(catalogId)?.catalog ?: throw CLIException("cli.catalog.not_found", catalogId)
    return ActiveCliGame(GameDriver(JSONImporter(catalog).import(content)), catalogId)
  }

  fun list(): List<String> {
    return root.listDirectoryEntries("*.json").map { it.nameWithoutExtension }.sorted()
  }

  fun delete(name: String) {
    val path = fileFor(name)
    if (!path.deleteIfExists()) {
      throw CLIException("cli.games.not_found", name)
    }
  }

  fun top(): List<TopEntry> {
    val wins = mutableMapOf<String, Int>()
    list().forEach { name ->
      val winner = load(name).driver.game.winner ?: return@forEach
      wins[winner.name] = (wins[winner.name] ?: 0) + 1
    }
    return wins.entries
        .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
        .map { TopEntry(it.key, it.value) }
  }

  private fun fileFor(name: String): Path {
    if (name.isBlank()) {
      throw CLIException("cli.games.invalid_name")
    }
    return root.resolve("$name.json")
  }

  private fun parseCatalogId(content: String): String {
    val payload: GameJson = JSONImporter.parsePayload(content)
    return payload.metadata?.catalogId ?: catalogs.defaultCatalogId
  }

  companion object {
    fun default(): CLIStorage {
      val root = Path.of(System.getProperty("user.dir"), ".machikoro-cli")
      Files.createDirectories(root)
      return CLIStorage(root, CLICatalogRegistry.default())
    }
  }
}
