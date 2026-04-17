package ru.kislball.machikoro.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import ru.kislball.machikoro.CountingEffect
import ru.kislball.machikoro.StubAction
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.cards.CardKind

class GameAndStepsTest {
  @Test
  fun `game constructor rejects empty player list`() {
    assertFailsWith<IllegalArgumentException> { Game(emptyList()) }
  }

  @Test
  fun `getTriggerables exposes all player cards`() {
    val p1 = Player("p1")
    val p2 = Player("p2")
    p1.cards.add(StubCard(CardKind.RANCH))
    p2.cards.add(StubCard(CardKind.BAKERY))
    val game = Game(listOf(p1, p2))

    val triggerables = game.getTriggerables().toList()

    assertEquals(2, triggerables.size)
  }

  @Test
  fun `countCardsOfKind counts cards across players`() {
    val p1 = Player("p1")
    val p2 = Player("p2")
    p1.cards.add(StubCard(CardKind.RANCH))
    p2.cards.add(StubCard(CardKind.RANCH))
    val game = Game(listOf(p1, p2))

    assertEquals(2, game.countCardsOfKind(CardKind.RANCH))
  }

  @Test
  fun `nextStep creates waiting step and rotates current player`() {
    val game = Game(listOf(Player("p1"), Player("p2")))

    val step1 = game.nextStep() as WaitingDiceStep
    val finished = FinishedActionStep(game, step1.rollDice(1), StubAction(step1.currentPlayer))
    game.steps.add(finished)
    val step2 = game.nextStep()

    assertEquals("p1", step1.currentPlayer.name)
    assertEquals("p2", step2.currentPlayer.name)
    assertNotNull(game.currentPlayer)
    assertEquals(step2, game.currentStep)
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
    val waiting = game.nextStep() as WaitingDiceStep

    val rolled = waiting.rollDice(2)

    assertEquals(3, rolled.dice.size)
    assertTrue(rolled.dice.all { it in 0..7 })
  }

  @Test
  fun `dice rolled step init applies effects for triggered cards`() {
    val effect = CountingEffect()
    val player = Player("p1")
    player.cards.add(StubCard(CardKind.RANCH, triggered = true, effect = effect))
    val game = Game(listOf(player))
    val waiting = game.nextStep() as WaitingDiceStep

    waiting.rollDice(1)

    assertEquals(1, effect.appliedCount)
  }

  @Test
  fun `finish delegates validation and returns finished action step`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val rolled = (game.nextStep() as WaitingDiceStep).rollDice(1)
    val action = StubAction(player)

    val finished = rolled.finish(action)

    assertEquals(2, action.checkValidCalled)
    assertEquals(player, finished.currentPlayer)
  }

  @Test
  fun `finished action step applies action effect`() {
    val effect = CountingEffect()
    val player = Player("p1")
    val game = Game(listOf(player))
    val rolled = (game.nextStep() as WaitingDiceStep).rollDice(1)

    FinishedActionStep(game, rolled, StubAction(player, effect))

    assertEquals(1, effect.appliedCount)
  }
}
