@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

package ru.kislball.machikoro.storage

import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.common.CardCatalogDefinition
import ru.kislball.machikoro.cards.common.CardCatalogResolver
import ru.kislball.machikoro.cards.common.OverrideStarterCardsCatalog
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.facility.GameFactory
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.triggers.special.SightsCollectedTrigger

class GameStorageTest {
  private val tempDir = createTempDirectory("machikoro-game-storage")

  @AfterTest
  fun tearDown() {
    tempDir.deleteRecursively()
  }

  @Test
  fun `storage saves loads lists and deletes games`() {
    val storage = storageFor()
    val game = GameFactory.createDriver(StandardCatalog, listOf("alice", "bob"))

    storage.save("demo", game, "standard")

    val listed = storage.list()
    assertEquals(listOf("demo"), listed.map { it.name })
    assertEquals(listOf("alice", "bob"), listed.single().playerNames)
    assertFalse(listed.single().finished)
    assertEquals(2, storage.load("demo").driver.game.players.size)
    assertTrue(tempDir.resolve("demo.json").readText().contains("\"catalogId\":\"standard\""))
    assertTrue(tempDir.resolve("demo.json").readText().contains("\"finished\":false"))

    storage.delete("demo")

    assertTrue(storage.list().isEmpty())
  }

  @Test
  fun `top counts wins from winner metadata`() {
    val storage = storageFor()
    val alice = Player("alice")
    val bob = Player("bob")

    storage.save(
        "finished1",
        GameDriver(Game(StandardCatalog, listOf(alice, bob), SightsCollectedTrigger(), alice)),
        "standard",
    )
    storage.save(
        "finished2",
        GameDriver(Game(StandardCatalog, listOf(alice, bob), SightsCollectedTrigger(), alice)),
        "standard",
    )
    storage.save(
        "unfinished",
        GameFactory.createDriver(StandardCatalog, listOf("alice", "bob")),
        "standard",
    )

    assertEquals(listOf(TopEntry("alice", 2)), storage.top())
  }

  @Test
  fun `storage loads saves with catalog id from json`() {
    val customCatalog =
        OverrideStarterCardsCatalog(
            StandardCatalog.getCardList(),
            listOf("cards.wheat", "cards.bakery", "cards.tv_tower"),
        )
    val storage =
        storageFor(
            mapOf(
                "standard" to StandardCatalog,
                "custom" to customCatalog,
            ))
    val game = GameFactory.createDriver(customCatalog, listOf("alice", "bob"))

    storage.save("custom-save", game, "custom")

    val loaded = storage.load("custom-save")
    assertEquals("custom", loaded.catalogId)
    assertEquals(
        listOf("cards.wheat", "cards.bakery", "cards.tv_tower"),
        loaded.driver.game.players.first().cards.map { it.cardId },
    )
  }

  @Test
  fun `storage falls back to winner metadata when finished flag is absent`() {
    tempDir
        .resolve("legacy.json")
        .writeText(
            """
            {"players":[{"name":"alice","balance":3,"cards":[]}],"metadata":{"winner":"alice","catalogId":"standard"}}
            """
                .trimIndent())
    val storage = storageFor()

    val listed = storage.list().single()
    val loaded = storage.load("legacy")

    assertTrue(listed.finished)
    assertTrue(loaded.driver.game.finished)
  }

  private fun storageFor(
      catalogs: Map<String, CardCatalog> = mapOf("standard" to StandardCatalog)
  ): GameStorage {
    return GameStorage(
        tempDir,
        "standard",
        CardCatalogResolver(catalogs.map { (id, catalog) -> CardCatalogDefinition(id, catalog) }),
    )
  }
}
