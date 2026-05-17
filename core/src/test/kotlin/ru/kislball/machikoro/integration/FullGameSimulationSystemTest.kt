package ru.kislball.machikoro.integration

import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.jetbrains.exposed.v1.jdbc.Database
import ru.kislball.machikoro.cards.common.CardCatalogResolver
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.effects.dice.RethrowDiceInputEffect
import ru.kislball.machikoro.effects.order.GivePlayerAdditionalStepInputEffect
import ru.kislball.machikoro.exceptions.NoFillerCardsAvailableException
import ru.kislball.machikoro.facility.GameFactory
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.IntermediateRollResult
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.markers.canRethrowDice
import ru.kislball.machikoro.game.markers.canThrowTwoDice
import ru.kislball.machikoro.game.step.PendingStepPhase
import ru.kislball.machikoro.storage.sql.SQLStorage

class FullGameSimulationSystemTest {
  private val fillerCards =
      listOf(
          "cards.wheat",
          "cards.farm",
          "cards.bakery",
          "cards.supermarket",
          "cards.cafe",
          "cards.family",
          "cards.reserve",
          "cards.mine",
          "cards.apples",
          "cards.cheese_factory",
          "cards.furniture",
          "cards.vegetables",
      )

  @Test
  fun `driver simulates a multi-turn game on standard catalog`() {
    val driver = GameFactory.createDriver(StandardCatalog, listOf("alice", "bob"))
    val alice = driver.game.players[0]
    val bob = driver.game.players[1]
    alice.balance = 1_000
    bob.balance = 1_000

    fun buyAnyAvailableFillerCard(player: Player) {
      val current = driver.game.currentStepPhase as PendingStepPhase
      var selectedCardId: String? = null
      for (cardId in fillerCards) {
        val card = driver.game.catalog[cardId] ?: continue
        if (driver.game.countCardsOfKind(card) >= card.totalCards) continue
        if (player.balance < card.getPrice(current)) continue
        selectedCardId = cardId
        break
      }

      if (selectedCardId == null) throw NoFillerCardsAvailableException()
      val finishedStep = driver.buyCard(player, selectedCardId)
      assertNotNull(finishedStep)
    }

    // Step 1
    driver.rollDice(alice, 1)
    val firstFinishedStep = driver.buyCard(alice, "cards.railway_station")
    assertNotNull(firstFinishedStep)
    assertTrue(alice.canThrowTwoDice())

    // Step 2
    driver.nextStep()
    driver.rollDice(bob, 1)
    val secondFinishedStep = driver.buyCard(bob, "cards.wheat")
    assertNotNull(secondFinishedStep)

    // Step 3
    driver.nextStep()
    driver.rollDice(alice, 1)
    val thirdFinishedStep = driver.buyCard(alice, "cards.tv_tower")
    assertNotNull(thirdFinishedStep)
    assertTrue(alice.canRethrowDice())

    assertFalse(driver.game.finished)

    // Step 4
    driver.nextStep()
    driver.rollDice(bob, 1)
    buyAnyAvailableFillerCard(bob)

    driver.nextStep()
    val step4AliceBeforeAlice = alice.balance
    val step4AliceBeforeBob = bob.balance
    driver.rollDice(alice, 1)
    assertTrue(driver.shouldAnswer<RethrowDiceInputEffect>(alice))
    val step4Turn = driver.game.currentStepPhase as PendingStepPhase
    step4Turn.results.set(IntermediateRollResult(DiceRollResult(alice, listOf(2))))
    driver.submitRethrowDecision(alice, shouldRethrow = false)
    assertEquals(step4AliceBeforeAlice + 1, alice.balance)
    assertEquals(step4AliceBeforeBob, bob.balance)
    val fourthFinishedStep = driver.buyCard(alice, "cards.entertainment_park")
    assertNotNull(fourthFinishedStep)
    assertTrue(alice.canThrowTwoDice())

    driver.nextStep()
    driver.rollDice(bob, 1)
    buyAnyAvailableFillerCard(bob)

    driver.nextStep()
    val loopAliceBeforeAlice = alice.balance
    val loopAliceBeforeBob = bob.balance
    val aliceTurn = driver.rollDice(alice, 2)
    assertTrue(driver.shouldAnswer<RethrowDiceInputEffect>(alice))
    assertTrue(driver.game.inputEffects.peek() is RethrowDiceInputEffect)
    aliceTurn.results.set(IntermediateRollResult(DiceRollResult(alice, listOf(3, 3))))
    driver.submitRethrowDecision(alice, shouldRethrow = false)
    assertEquals(loopAliceBeforeAlice, alice.balance)
    assertEquals(loopAliceBeforeBob, bob.balance)

    assertTrue(driver.game.inputEffects.peek() is GivePlayerAdditionalStepInputEffect)
    val beforeAdditionalStepDecisionAlice = alice.balance
    val beforeAdditionalStepDecisionBob = bob.balance
    driver.submitAdditionalStepDecision(alice, true)
    assertEquals(beforeAdditionalStepDecisionAlice, alice.balance)
    assertEquals(beforeAdditionalStepDecisionBob, bob.balance)
    aliceTurn.results.set(DiceRollResult(alice, listOf(3, 4)))
    buyAnyAvailableFillerCard(alice)

    val additionalStep = driver.nextStep()
    assertTrue(additionalStep.currentPlayer == alice)
    val additionalStepBeforeAlice = alice.balance
    val additionalStepBeforeBob = bob.balance
    driver.rollDice(alice, 1)
    assertTrue(driver.shouldAnswer<RethrowDiceInputEffect>(alice))
    additionalStep.results.set(IntermediateRollResult(DiceRollResult(alice, listOf(1))))
    driver.submitRethrowDecision(alice, shouldRethrow = false)
    assertEquals(additionalStepBeforeAlice + 1, alice.balance)
    assertEquals(additionalStepBeforeBob + 3, bob.balance)
    buyAnyAvailableFillerCard(alice)

    // Step 5
    driver.nextStep()
    driver.rollDice(bob, 1)
    buyAnyAvailableFillerCard(bob)

    driver.nextStep()
    driver.rollDice(alice, 1)
    assertTrue(driver.shouldAnswer<RethrowDiceInputEffect>(alice))
    driver.submitRethrowDecision(alice, shouldRethrow = false)
    buyAnyAvailableFillerCard(alice)
    assertTrue(alice.canRethrowDice())

    // Step 6
    driver.nextStep()
    driver.rollDice(bob, 1)
    buyAnyAvailableFillerCard(bob)

    driver.nextStep()
    driver.rollDice(alice, 1)
    assertTrue(driver.shouldAnswer<RethrowDiceInputEffect>(alice))
    assertTrue(driver.game.inputEffects.peek() is RethrowDiceInputEffect)
    driver.submitRethrowDecision(alice, shouldRethrow = false)
    buyAnyAvailableFillerCard(alice)

    val aliceCards = alice.cards.map { it.cardId }.toSet()
    assertTrue("cards.railway_station" in aliceCards)
    assertTrue("cards.entertainment_park" in aliceCards)
    assertTrue("cards.tv_tower" in aliceCards)

    val bobCards = bob.cards.map { it.cardId }.toSet()
    assertTrue("cards.wheat" in bobCards)
    assertTrue("cards.farm" in bobCards || "cards.bakery" in bobCards || "cards.wheat" in bobCards)

    assertFalse(driver.game.steps.isEmpty())
    assertNotNull(driver.game.currentStepPhase)
    assertNotNull(driver.game.steps.last())
    assertTrue(driver.game.inputEffects.peek() == null)
  }

  @Test
  fun `saved sql checkpoint can be loaded and continued to a winner`() {
    Database.connect(
        url = "jdbc:h2:mem:${UUID.randomUUID()};DB_CLOSE_DELAY=-1",
        driver = "org.h2.Driver",
    )
    val storage = SQLStorage("standard", CardCatalogResolver.default)
    val driver = GameFactory.createDriver(StandardCatalog, listOf("alice"), initialBalance = 100)
    val alice = driver.game.players.single()

    driver.rollDice(alice, 1)
    driver.buyCard(alice, "cards.railway_station")
    driver.nextStep()
    driver.rollDice(alice, 1)
    driver.buyCard(alice, "cards.shopping_centre")
    storage.save("checkpoint", driver, "standard")

    val loadedDriver = storage.load("checkpoint").driver
    val loadedAlice = loadedDriver.game.players.single()
    assertEquals(
        listOf(
            "cards.wheat",
            "cards.bakery",
            "cards.railway_station",
            "cards.shopping_centre",
        ),
        loadedAlice.cards.map { it.cardId },
    )

    loadedDriver.rollDice(loadedAlice, 1)
    loadedDriver.buyCard(loadedAlice, "cards.entertainment_park")
    loadedDriver.nextStep()
    loadedDriver.rollDice(loadedAlice, 1)
    loadedDriver.buyCard(loadedAlice, "cards.tv_tower")
    storage.save("checkpoint", loadedDriver, "standard")

    assertTrue(loadedDriver.game.finished)
    assertEquals("alice", loadedDriver.game.winner?.name)
    val summary = storage.list().single()
    assertEquals("checkpoint", summary.name)
    assertTrue(summary.finished)
    assertEquals("alice", summary.winnerName)
    assertEquals("alice", storage.load("checkpoint").driver.game.winner?.name)
  }
}
