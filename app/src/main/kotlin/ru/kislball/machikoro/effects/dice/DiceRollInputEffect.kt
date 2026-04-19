package ru.kislball.machikoro.effects.dice

import ru.kislball.machikoro.effects.input.InputEffect
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.IntermediateRollResult
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.contains
import ru.kislball.machikoro.game.markers.canRethrowDice
import ru.kislball.machikoro.game.markers.canThrowTwoDice
import ru.kislball.machikoro.game.step.StepPhase

class DiceRollInputEffect(player: Player) : InputEffect<Int>("effects.dice_roll_input", player) {
  override fun checkInput(input: Int): Boolean {
    require(input in 1..2) { "Input must be between 1 and 2" }
    require(input != 2 || player.canThrowTwoDice()) { "Player ${player.name} can't throw two dice" }
    return true
  }

  override fun applyWithInput(stepPhase: StepPhase, input: Int) {
    val dice = (1..input).map { (DICE_MIN_VALUE..DICE_MAX_VALUE).random() }
    val result = DiceRollResult(player, dice)

    if (player.canRethrowDice() && !stepPhase.results.contains<IntermediateRollResult>()) {
      stepPhase.results.set(IntermediateRollResult(result))
      RethrowDiceInputEffect.getAwaiter(player).apply(stepPhase)
    } else {
      stepPhase.results.set(result)
    }
  }

  private companion object {
    const val DICE_MIN_VALUE = 1
    const val DICE_MAX_VALUE = 6
  }
}
