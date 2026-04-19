package ru.kislball.machikoro.effects.utility.input

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import ru.kislball.machikoro.CountingEffect
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.effects.input.AwaitInputEffect
import ru.kislball.machikoro.effects.input.InputEffect
import ru.kislball.machikoro.effects.input.ProvideInputEffect
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class InputEffectsTest {
  @Test
  fun `await input effect enqueues target effect`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val step = game.nextStep()
    val inputEffect = RecordingIntInputEffect(player)

    AwaitInputEffect("effects.await_input", player, inputEffect).apply(step)

    assertEquals(inputEffect, game.inputEffects.peek())
  }

  @Test
  fun `input effect getEffect validates input and applies payload`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val step = game.nextStep()
    val inputEffect = RecordingIntInputEffect(player)

    assertFailsWith<IllegalArgumentException> { inputEffect.getEffect(-1) }

    val effect = inputEffect.getEffect(2)
    effect.apply(step)

    assertEquals(listOf(2), inputEffect.appliedInputs)
  }

  @Test
  fun `provide input effect dequeues and applies on valid input from owner`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val step = game.nextStep()
    val inputEffect = RecordingIntInputEffect(player)
    game.inputEffects.enqueue(inputEffect)

    ProvideInputEffect(inputEffect, 3, player).apply(step)

    assertNull(game.inputEffects.peek())
    assertEquals(listOf(3), inputEffect.appliedInputs)
  }

  @Test
  fun `provide input effect throws on invalid input`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val step = game.nextStep()
    val inputEffect = RecordingIntInputEffect(player)
    game.inputEffects.enqueue(inputEffect)

    assertFailsWith<IllegalArgumentException> {
      ProvideInputEffect(inputEffect, -1, player).apply(step)
    }

    assertEquals(inputEffect, game.inputEffects.peek())
    assertEquals(emptyList(), inputEffect.appliedInputs)
  }

  @Test
  fun `provide input effect throws on input from non-owner player`() {
    val owner = Player("owner")
    val other = Player("other")
    val game = Game(listOf(owner, other))
    val step = game.nextStep()
    val inputEffect = RecordingIntInputEffect(owner)
    game.inputEffects.enqueue(inputEffect)

    assertFailsWith<IllegalStateException> { ProvideInputEffect(inputEffect, 4, other).apply(step) }

    assertEquals(inputEffect, game.inputEffects.peek())
    assertEquals(emptyList(), inputEffect.appliedInputs)
  }

  @Test
  fun `dice rolled finish returns null while awaiting input`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val driver = GameDriver(game)
    val rolled = driver.nextStep()
    driver.rollDice(player, 1)
    val inputEffect = RecordingIntInputEffect(player)

    val result = rolled.submitPlayerAction(AwaitingInputAction(player, inputEffect))

    assertNull(result)
    assertEquals(rolled, game.currentStepPhase)
    assertEquals(inputEffect, game.inputEffects.peek())
  }

  @Test
  fun `driver finishStep returns null while awaiting input`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val driver = GameDriver(game)
    val rolled = driver.rollDice(player, 1)
    val inputEffect = RecordingIntInputEffect(player)

    val result = driver.finishStep(AwaitingInputAction(player, inputEffect))

    assertNull(result)
    assertEquals(rolled, game.currentStepPhase)
    assertNotNull(game.inputEffects.peek())
  }

  @Test
  fun `provide input effect resolves queued input from awaiting flow`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val driver = GameDriver(game)
    val rolled = driver.nextStep()
    driver.rollDice(player, 1)
    val inputEffect = RecordingIntInputEffect(player)

    val finishResult = rolled.submitPlayerAction(AwaitingInputAction(player, inputEffect))
    assertNull(finishResult)
    assertEquals(inputEffect, game.inputEffects.peek())

    ProvideInputEffect(inputEffect, 6, player).apply(rolled)

    assertNull(game.inputEffects.peek())
    assertEquals(listOf(6), inputEffect.appliedInputs)
  }

  @Test
  fun `provide input effect checks input effect validity against step phase`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val step = game.nextStep()
    val inputEffect = InvalidOnStepInputEffect(player)
    game.inputEffects.enqueue(inputEffect)

    assertFailsWith<IllegalArgumentException> {
      ProvideInputEffect(inputEffect, 1, player).apply(step)
    }

    assertEquals(inputEffect, game.inputEffects.peek())
    assertEquals(emptyList(), inputEffect.appliedInputs)
  }

  @Test
  fun `provide input effect throws for wrong player in awaiting flow`() {
    val owner = Player("owner")
    val other = Player("other")
    val game = Game(listOf(owner, other))
    val driver = GameDriver(game)
    val rolled = driver.nextStep()
    driver.rollDice(owner, 1)
    val inputEffect = RecordingIntInputEffect(owner)

    val finishResult = rolled.submitPlayerAction(AwaitingInputAction(owner, inputEffect))
    assertNull(finishResult)
    assertEquals(inputEffect, game.inputEffects.peek())

    assertFailsWith<IllegalStateException> {
      ProvideInputEffect(inputEffect, 6, other).apply(rolled)
    }

    assertEquals(inputEffect, game.inputEffects.peek())
    assertEquals(emptyList(), inputEffect.appliedInputs)
  }

  @Test
  fun `provide input does not rerun triggerables by itself`() {
    val effect = CountingEffect()
    val player = Player("p1")
    player.cards.add(StubCard("cards.ranch", triggered = true, effect = effect))
    val game = Game(listOf(player))
    val step = game.nextStep()
    val inputEffect = RecordingIntInputEffect(player)
    game.inputEffects.enqueue(inputEffect)

    ProvideInputEffect(inputEffect, 1, player).apply(step)

    assertEquals(1, effect.appliedCount)
  }

}

private class RecordingIntInputEffect(player: Player) :
    InputEffect<Int>("effects.test.input", player) {
  val appliedInputs = mutableListOf<Int>()

  override fun checkInput(input: Int): Boolean {
    return input > 0
  }

  override fun applyWithInput(stepPhase: StepPhase, input: Int) {
    appliedInputs.add(input)
  }
}

private class InvalidOnStepInputEffect(player: Player) :
    InputEffect<Int>("effects.test.input.invalid_on_step", player) {
  val appliedInputs = mutableListOf<Int>()

  override fun checkInput(input: Int): Boolean {
    return true
  }

  override fun isValid(stepPhase: StepPhase, input: Int): Boolean {
    return false
  }

  override fun applyWithInput(stepPhase: StepPhase, input: Int) {
    appliedInputs.add(input)
  }
}

private class AwaitingInputAction(player: Player, private val inputEffect: InputEffect<Int>) :
    PlayerAction("actions.test.await_input", player) {
  override fun checkValid(s: StepPhase) = Unit

  override fun getEffect(s: StepPhase) =
      AwaitInputEffect("effects.await_input", player, inputEffect)
}
