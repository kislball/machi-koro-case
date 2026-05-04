package ru.kislball.machikoro.triggers

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.kislball.machikoro.StubTrigger
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.game.utilities.get
import ru.kislball.machikoro.triggers.dice.AnyDiceTrigger
import ru.kislball.machikoro.triggers.dice.PlayerDiceTrigger
import ru.kislball.machikoro.triggers.utility.AndTrigger
import ru.kislball.machikoro.triggers.utility.OrTrigger

class TriggerTest {
  private fun testStep(game: Game, player: Player): StepPhase =
      object : StepPhase(game, player, 1) {}

  @Test
  fun `any dice trigger fires for matching rolled value`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val rolled =
        GameDriver(game).run {
          val pending = nextStep()
          rollDice(player, 1)
          pending
        }
    val trigger = AnyDiceTrigger(rolled.results.get<DiceRollResult>().diceThrown)

    assertTrue(trigger.isTriggered(rolled, null))
    assertEquals("triggers.any-dice.name", trigger.triggerNameKey)
    assertEquals("triggers.any-dice.description", trigger.triggerDescriptionKey)
  }

  @Test
  fun `any dice trigger ignores non dice step`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val step = testStep(game, player)

    assertFalse(AnyDiceTrigger(listOf(1)).isTriggered(step, null))
  }

  @Test
  fun `player dice trigger requires matching player and dice`() {
    val p1 = Player("p1")
    val p2 = Player("p2")
    val game = Game(listOf(p1, p2))
    val rolled =
        GameDriver(game).run {
          val pending = nextStep()
          rollDice(p1, 1)
          pending
        }
    val trigger = PlayerDiceTrigger(p1, rolled.results.get<DiceRollResult>().diceThrown)

    assertTrue(trigger.isTriggered(rolled, null))
    assertFalse(
        PlayerDiceTrigger(p2, rolled.results.get<DiceRollResult>().diceThrown)
            .isTriggered(rolled, null))
  }

  @Test
  fun `dice triggers match roll sum for two dice`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val step = testStep(game, player).apply { results.set(DiceRollResult(player, listOf(3, 4))) }

    assertTrue(AnyDiceTrigger(7).isTriggered(step, null))
    assertTrue(PlayerDiceTrigger(player, 7).isTriggered(step, null))
    assertFalse(AnyDiceTrigger(3).isTriggered(step, null))
    assertFalse(PlayerDiceTrigger(player, 4).isTriggered(step, null))
  }

  @Test
  fun `and trigger requires all nested triggers`() {
    val step = testStep(Game(listOf(Player("p1"))), Player("p1"))

    assertTrue(AndTrigger(listOf(StubTrigger(true), StubTrigger(true))).isTriggered(step, null))
    assertFalse(AndTrigger(listOf(StubTrigger(true), StubTrigger(false))).isTriggered(step, null))
  }

  @Test
  fun `or trigger requires at least one nested trigger`() {
    val step = testStep(Game(listOf(Player("p1"))), Player("p1"))

    assertTrue(OrTrigger(listOf(StubTrigger(false), StubTrigger(true))).isTriggered(step, null))
    assertFalse(OrTrigger(listOf(StubTrigger(false), StubTrigger(false))).isTriggered(step, null))
  }
}
