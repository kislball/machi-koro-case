package ru.kislball.machikoro.integration

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.common.CardCatalogDefinition
import ru.kislball.machikoro.cards.common.CardCatalogResolver
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.exceptions.GameException
import ru.kislball.machikoro.facility.GameFactory
import ru.kislball.machikoro.facility.payload.GamePayload
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.storage.json.JSONExporter
import ru.kislball.machikoro.storage.json.JSONImporter

class GameFlowIntegrationTest {
  @Test
  fun `player can buy card through driver flow`() {
    val catalog = CardCatalog(StubCard("cards.tv_station"))
    val driver = GameFactory.createDriver(catalog, listOf("alice", "bob"))
    val alice = driver.game.players.first()
    alice.balance = 10

    driver.rollDice(alice, 1)
    driver.buyCard(alice, "cards.tv_station")

    assertEquals(9, alice.balance)
    assertEquals("cards.tv_station", alice.cards.single().cardId)
    assertEquals(1, driver.game.steps.size)
  }

  @Test
  fun `export and import preserve game players balances and cards`() {
    val catalog = CardCatalog(StubCard("cards.business_center"), StubCard("cards.cafe"))

    val alice = Player("alice")
    alice.balance = 4
    alice.cards.add(StubCard("cards.business_center"))

    val bob = Player("bob")
    bob.balance = 2
    bob.cards.add(StubCard("cards.cafe"))

    val game = Game(listOf(alice, bob))

    val exported = JSONExporter().export(GamePayload(game))
    val payload = JSONImporter().import(exported)
    payload.catalogResolver = CardCatalogResolver(CardCatalogDefinition("standard", catalog))
    val imported = GameFactory.createDriver(payload).game

    val importedPlayers = imported.players
    assertEquals(listOf("alice", "bob"), importedPlayers.map { it.name })
    assertEquals(listOf(4, 2), importedPlayers.map { it.balance })
    assertEquals(
        listOf("cards.business_center", "cards.cafe"),
        importedPlayers.map { it.cards.single().cardId })
  }

  @Test
  fun `player wins game after buying all sight cards`() {
    val driver = GameFactory.createDriver(StandardCatalog, listOf("alice"), initialBalance = 100)
    val player = driver.game.players.single()
    val sights =
        listOf(
            "cards.railway_station",
            "cards.shopping_centre",
            "cards.entertainment_park",
            "cards.tv_tower")

    for (cardId in sights) {
      driver.rollDice(player, 1)
      val finished = driver.buyCard(player, cardId)
      assertNotNull(finished)
      if (!driver.game.finished) {
        driver.nextStep()
      }
    }

    assertTrue(driver.game.finished)
    assertEquals(player, driver.game.winner)
    assertFailsWith<GameException> { driver.rollDice(player, 1) }
  }

  @Test
  fun `finished game survives export import and remains closed for new turns`() {
    val driver = GameFactory.createDriver(StandardCatalog, listOf("alice"), initialBalance = 100)
    val player = driver.game.players.single()

    listOf(
            "cards.railway_station",
            "cards.shopping_centre",
            "cards.entertainment_park",
            "cards.tv_tower",
        )
        .forEach { cardId ->
          driver.rollDice(player, 1)
          driver.buyCard(player, cardId)
          if (!driver.game.finished) {
            driver.nextStep()
          }
        }

    val exported = JSONExporter().export(GamePayload(driver.game))
    val payload = JSONImporter().import(exported)
    payload.catalogResolver = CardCatalogResolver.default
    val importedDriver = GameFactory.createDriver(payload)

    assertTrue(importedDriver.game.finished)
    assertEquals("alice", importedDriver.game.winner?.name)
    assertEquals(
        listOf(
            "cards.wheat",
            "cards.bakery",
            "cards.railway_station",
            "cards.shopping_centre",
            "cards.entertainment_park",
            "cards.tv_tower",
        ),
        importedDriver.game.players.single().cards.map { it.cardId },
    )
    assertFailsWith<GameException> {
      importedDriver.rollDice(importedDriver.game.players.single(), 1)
    }
  }
}
