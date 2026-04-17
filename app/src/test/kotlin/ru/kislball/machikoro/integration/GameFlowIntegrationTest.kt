package ru.kislball.machikoro.integration

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.actions.BuyCardAction
import ru.kislball.machikoro.cards.CardCatalog
import ru.kislball.machikoro.cards.CardKind
import ru.kislball.machikoro.facility.GameFactory
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player

class GameFlowIntegrationTest {
  @Test
  fun `player can buy card through driver flow`() {
    CardCatalog.registerCreator(CardKind.TV_STATION) { StubCard(CardKind.TV_STATION) }
    val driver = GameFactory.createDriver(listOf("alice", "bob"))
    val alice = driver.game.players.first()
    alice.balance = 10

    driver.rollDice(alice, 1)
    driver.finishStep(BuyCardAction(alice, CardKind.TV_STATION))

    assertEquals(10 - CardKind.TV_STATION.basePrice, alice.balance)
    assertEquals(CardKind.TV_STATION, alice.cards.single().kind)
    assertTrue(driver.game.steps.size >= 3)
  }

  @Test
  fun `export and import preserve game players balances and cards`() {
    CardCatalog.registerCreator(CardKind.BUSINESS_CENTER) { StubCard(CardKind.BUSINESS_CENTER) }
    CardCatalog.registerCreator(CardKind.CAFE) { StubCard(CardKind.CAFE) }

    val alice = Player("alice")
    alice.balance = 4
    alice.cards.add(StubCard(CardKind.BUSINESS_CENTER))

    val bob = Player("bob")
    bob.balance = 2
    bob.cards.add(StubCard(CardKind.CAFE))

    val game = Game(listOf(alice, bob))

    val exported = GameFactory.export(game)
    val importedDriver = GameFactory.import(exported)

    val importedPlayers = importedDriver.game.players
    assertEquals(listOf("alice", "bob"), importedPlayers.map { it.name })
    assertEquals(listOf(4, 2), importedPlayers.map { it.balance })
    assertEquals(
        listOf(CardKind.BUSINESS_CENTER, CardKind.CAFE),
        importedPlayers.map { it.cards.single().kind },
    )
  }
}

