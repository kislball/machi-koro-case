package ru.kislball.machikoro.effects.utility.input

import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class DiceRollInputEffect(player: Player) : InputEffect<List<Int>>("effects.dice_roll_input", player) {
  override fun checkInput(input: List<Int>): Boolean {
    return input.all { it in DICE_MIN_VALUE..DICE_MAX_VALUE }
  }

  override fun applyWithInput(stepPhase: StepPhase, input: List<Int>) {
    stepPhase.results.set(DiceRollResult(player = player, diceThrown = input))
  }

  private companion object {
    const val DICE_MIN_VALUE = 0
    const val DICE_MAX_VALUE = 7
  }
}

