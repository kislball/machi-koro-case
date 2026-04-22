package ru.kislball.machikoro.integration

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.actions.BuyCardAction
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.facility.GameFactory
import ru.kislball.machikoro.facility.json.JSONExporter
import ru.kislball.machikoro.facility.json.JSONImporter
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player

class GameFlowIntegrationTest {
  @Test
  fun `player can buy card through driver flow`() {
    val catalog = CardCatalog(StubCard("cards.tv_station"))
    val driver = GameFactory.createDriver(catalog, listOf("alice", "bob"))
    val alice = driver.game.players.first()
    alice.balance = 10

    driver.rollDice(alice, 1)
    driver.finishStep(BuyCardAction(driver.game, alice, "cards.tv_station"))

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

    val exported = JSONExporter().export(game)
    val imported = JSONImporter(catalog).import(exported)

    val importedPlayers = imported.players
    assertEquals(listOf("alice", "bob"), importedPlayers.map { it.name })
    assertEquals(listOf(4, 2), importedPlayers.map { it.balance })
    assertEquals(
        listOf("cards.business_center", "cards.cafe"),
        importedPlayers.map { it.cards.single().cardId })
  }
}
