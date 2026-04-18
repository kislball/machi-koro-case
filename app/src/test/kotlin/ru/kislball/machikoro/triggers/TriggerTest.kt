package ru.kislball.machikoro.triggers

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.kislball.machikoro.StubTrigger
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.game.step.WaitingDiceStepPhase

class TriggerTest {
  private fun testStep(game: Game, player: Player, number: Int): StepPhase =
      object : StepPhase(game, player, number) {}

  @Test
  fun `any dice trigger fires for matching rolled value`() {
    val player = Player("p1")
    val rolled = (Game(listOf(player)).nextStep() as WaitingDiceStepPhase).rollDice(1)
    val trigger = AnyDiceTrigger(rolled.dice)

    assertTrue(trigger.isTriggered(rolled, null))
    assertEquals("triggers.any-dice.name", trigger.triggerNameKey)
    assertEquals("triggers.any-dice.description", trigger.triggerDescriptionKey)
  }

  @Test
  fun `any dice trigger ignores non dice step`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val step = testStep(game, player, 1)

    assertFalse(AnyDiceTrigger(listOf(1)).isTriggered(step, null))
  }

  @Test
  fun `player dice trigger requires matching player and dice`() {
    val p1 = Player("p1")
    val p2 = Player("p2")
    val game = Game(listOf(p1, p2))
    val rolled = (game.nextStep() as WaitingDiceStepPhase).rollDice(1)
    val trigger = PlayerDiceTrigger(p1, rolled.dice)

    assertTrue(trigger.isTriggered(rolled, null))
    assertFalse(PlayerDiceTrigger(p2, rolled.dice).isTriggered(rolled, null))
  }

  @Test
  fun `and trigger requires all nested triggers`() {
    val step = testStep(Game(listOf(Player("p1"))), Player("p1"), 1)

    assertTrue(AndTrigger(listOf(StubTrigger(true), StubTrigger(true))).isTriggered(step, null))
    assertFalse(AndTrigger(listOf(StubTrigger(true), StubTrigger(false))).isTriggered(step, null))
  }

  @Test
  fun `or trigger requires at least one nested trigger`() {
    val step = testStep(Game(listOf(Player("p1"))), Player("p1"), 1)

    assertTrue(OrTrigger(listOf(StubTrigger(false), StubTrigger(true))).isTriggered(step, null))
    assertFalse(OrTrigger(listOf(StubTrigger(false), StubTrigger(false))).isTriggered(step, null))
  }
}
