@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

package ru.kislball.machikoro.cli

import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively
import kotlin.io.path.readText
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.kislball.machikoro.cards.common.OverrideStarterCardsCatalog
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.cli.catalog.CLICatalogDefinition
import ru.kislball.machikoro.cli.catalog.CLICatalogRegistry
import ru.kislball.machikoro.cli.session.ActiveCliGame
import ru.kislball.machikoro.cli.storage.CLIStorage
import ru.kislball.machikoro.cli.storage.TopEntry
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.facility.GameFactory
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.triggers.special.SightsCollectedTrigger

class CLIStorageTest {
  private val tempDir = createTempDirectory("machikoro-cli-storage")
  private val catalogs = CLICatalogRegistry.default()

  @AfterTest
  fun tearDown() {
    tempDir.deleteRecursively()
  }

  @Test
  fun `storage saves loads lists and deletes games`() {
    val storage = CLIStorage(tempDir, catalogs)
    val game =
        ActiveCliGame(GameFactory.createDriver(StandardCatalog, listOf("alice", "bob")), "standard")

    storage.save("demo", game)

    assertEquals(listOf("demo"), storage.list())
    assertEquals(2, storage.load("demo").driver.game.players.size)
    assertTrue(tempDir.resolve("demo.json").readText().contains("\"catalogId\":\"standard\""))

    storage.delete("demo")

    assertTrue(storage.list().isEmpty())
  }

  @Test
  fun `top counts wins from winner metadata`() {
    val storage = CLIStorage(tempDir, catalogs)
    val alice = Player("alice")
    val bob = Player("bob")

    storage.save(
        "finished1",
        ActiveCliGame(
            GameDriver(Game(StandardCatalog, listOf(alice, bob), SightsCollectedTrigger(), alice)),
            "standard"))
    storage.save(
        "finished2",
        ActiveCliGame(
            GameDriver(Game(StandardCatalog, listOf(alice, bob), SightsCollectedTrigger(), alice)),
            "standard"))
    storage.save(
        "unfinished",
        ActiveCliGame(
            GameFactory.createDriver(StandardCatalog, listOf("alice", "bob")), "standard"))

    assertEquals(listOf(TopEntry("alice", 2)), storage.top())
  }

  @Test
  fun `storage loads saves with catalog id from json`() {
    val customCatalog =
        OverrideStarterCardsCatalog(
            StandardCatalog.getCardList(),
            listOf("cards.wheat", "cards.bakery", "cards.tv_tower"),
        )
    val registry =
        CLICatalogRegistry(
            definitions =
                listOf(
                    CLICatalogDefinition("standard", StandardCatalog),
                    CLICatalogDefinition("custom", customCatalog),
                ),
            defaultCatalogId = "standard",
        )
    val storage = CLIStorage(tempDir, registry)
    val game =
        ActiveCliGame(GameFactory.createDriver(customCatalog, listOf("alice", "bob")), "custom")

    storage.save("custom-save", game)

    val loaded = storage.load("custom-save")
    assertEquals("custom", loaded.catalogId)
    assertEquals(
        listOf("cards.wheat", "cards.bakery", "cards.tv_tower"),
        loaded.driver.game.players.first().cards.map { it.cardId },
    )
  }
}
