package ru.kislball.machikoro.storage.sql

import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.jetbrains.exposed.v1.jdbc.Database
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.common.CardCatalogDefinition
import ru.kislball.machikoro.cards.common.CardCatalogResolver
import ru.kislball.machikoro.cards.common.OverrideStarterCardsCatalog
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.facility.GameFactory
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.storage.GameStorage
import ru.kislball.machikoro.storage.TopEntry
import ru.kislball.machikoro.triggers.special.SightsCollectedTrigger

class SQLStorageTest {
  @BeforeTest
  fun connectDatabase() {
    Database.connect(
        url = "jdbc:h2:mem:${UUID.randomUUID()};DB_CLOSE_DELAY=-1",
        driver = "org.h2.Driver",
    )
  }

  @Test
  fun `storage saves loads lists and deletes unfinished games`() {
    val storage = storageFor()
    val game = GameFactory.createDriver(StandardCatalog, listOf("alice", "bob"))

    storage.save("demo", game, "standard")

    val listed = storage.list()
    assertEquals(listOf("demo"), listed.map { it.name })
    assertEquals(listOf("alice", "bob"), listed.single().playerNames)
    assertFalse(listed.single().finished)
    assertEquals("standard", listed.single().catalogId)

    val loaded = storage.load("demo")
    assertEquals("standard", loaded.catalogId)
    assertEquals(listOf("alice", "bob"), loaded.driver.game.players.map { it.name })
    assertEquals(
        listOf("cards.wheat", "cards.bakery"),
        loaded.driver.game.players.first().cards.map { it.cardId },
    )

    storage.delete("demo")

    assertTrue(storage.list().isEmpty())
  }

  @Test
  fun `storage loads the requested save by name`() {
    val storage = storageFor()
    val first = GameFactory.createDriver(StandardCatalog, listOf("alice", "bob"))
    val second = GameFactory.createDriver(StandardCatalog, listOf("carol", "dave"))

    storage.save("first", first, "standard")
    storage.save("second", second, "standard")

    assertEquals(
        listOf("carol", "dave"), storage.load("second").driver.game.players.map { it.name })
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
  fun `storage loads saves with catalog id from sql`() {
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

  private fun storageFor(
      catalogs: Map<String, CardCatalog> = mapOf("standard" to StandardCatalog)
  ): GameStorage {
    return SQLStorage(
        "standard",
        CardCatalogResolver(catalogs.map { (id, catalog) -> CardCatalogDefinition(id, catalog) }),
    )
  }
}
