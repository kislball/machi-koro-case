package ru.kislball.machikoro.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import ru.kislball.machikoro.CountingEffect
import ru.kislball.machikoro.StubAction
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.game.markers.setThrowTwoDice
import ru.kislball.machikoro.game.utilities.get

class GameAndStepsTest {
  @Test
  fun `game constructor rejects empty player list`() {
    assertFailsWith<IllegalArgumentException> { Game(emptyList()) }
  }

  @Test
  fun `getTriggerables exposes all player cards`() {
    val p1 = Player("p1")
    val p2 = Player("p2")
    p1.cards.add(StubCard("cards.ranch"))
    p2.cards.add(StubCard("cards.bakery"))
    val game = Game(listOf(p1, p2))

    val triggerables = game.getTriggerables().toList()

    assertEquals(3, triggerables.size)
  }

  @Test
  fun `countCardsOfKind counts cards across players`() {
    val p1 = Player("p1")
    val p2 = Player("p2")
    p1.cards.add(StubCard("cards.ranch"))
    p2.cards.add(StubCard("cards.ranch"))
    val game = Game(listOf(p1, p2))

    assertEquals(2, game.countCardsOfKind("cards.ranch"))
  }

  @Test
  fun `nextStep creates waiting step and rotates current player`() {
    val game = Game(listOf(Player("p1"), Player("p2")))
    val driver = GameDriver(game)

    val step1 = driver.nextStep()
    driver.rollDice(step1.currentPlayer, 1)
    val finished = step1.submitPlayerAction(StubAction(step1.currentPlayer))
    assertNotNull(finished)
    val step2 = game.nextStep()

    assertEquals("p1", step1.currentPlayer.name)
    assertEquals(0, step1.stepNumber)
    assertEquals("p2", step2.currentPlayer.name)
    assertEquals(1, step2.stepNumber)
    assertNotNull(game.currentPlayer)
    assertEquals(step2, game.currentStepPhase)
  }

  @Test
  fun `nextStep fails if previous action is not finished`() {
    val game = Game(listOf(Player("p1")))
    game.nextStep()

    assertFailsWith<Exception> { game.nextStep() }
  }

  @Test
  fun `waiting dice step rollDice returns random dice list of expected size`() {
    val game = Game(listOf(Player("p1")))
    val driver = GameDriver(game)
    val waiting = driver.nextStep()
    waiting.currentPlayer.setThrowTwoDice(true)

    driver.rollDice(waiting.currentPlayer, 2)
    val rolled = waiting.results.get<DiceRollResult>()

    assertEquals(2, rolled.diceThrown.size)
    assertTrue(rolled.diceThrown.all { it in 1..6 })
  }

  @Test
  fun `dice rolled step init applies effects for triggered cards`() {
    val effect = CountingEffect()
    val player = Player("p1")
    player.cards.add(StubCard("cards.ranch", triggered = true, effect = effect))
    val game = Game(listOf(player))
    val driver = GameDriver(game)
    driver.nextStep()

    driver.rollDice(player, 1)

    assertEquals(2, effect.appliedCount)
  }

  @Test
  fun `finish delegates validation and returns finished action step`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val driver = GameDriver(game)
    val waiting = driver.nextStep()
    driver.rollDice(player, 1)
    val action = StubAction(player)

    val finished = waiting.submitPlayerAction(action)

    assertEquals(1, action.checkValidCalled)
    assertNotNull(finished)
    assertEquals(player, finished.currentPlayer)
  }

  @Test
  fun `finished action step applies action effect`() {
    val effect = CountingEffect()
    val player = Player("p1")
    val game = Game(listOf(player))
    val driver = GameDriver(game)
    val waiting = driver.nextStep()
    driver.rollDice(player, 1)

    val finished = waiting.submitPlayerAction(StubAction(player, effect))

    assertNotNull(finished)
    assertEquals(1, effect.appliedCount)
  }
}
