package ru.kislball.machikoro.effects.dice

import ru.kislball.machikoro.effects.input.AwaitInputEffect
import ru.kislball.machikoro.effects.input.InputEffect
import ru.kislball.machikoro.exceptions.DiceAlreadyRolledException
import ru.kislball.machikoro.exceptions.check
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.IntermediateRollResult
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.markers.canRethrowDice
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.game.utilities.contains
import ru.kislball.machikoro.game.utilities.get
import ru.kislball.machikoro.game.utilities.remove

class RethrowDiceInputEffect(player: Player) :
    InputEffect<Boolean>("effects.dice.rethrow", player) {
  override fun applyWithInput(stepPhase: StepPhase, input: Boolean) {
    val intermediate = stepPhase.results.get<IntermediateRollResult>().result
    if (input) {
      check(player.canRethrowDice()) { DiceAlreadyRolledException() }
      check(!stepPhase.results.contains<DiceRollResult>()) { DiceAlreadyRolledException() }
      DiceRollInputEffect(player).applyWithInput(stepPhase, intermediate.diceThrown.size)
    } else {
      stepPhase.results.set(intermediate)
    }
    stepPhase.results.remove<IntermediateRollResult>()
  }

  companion object {
    fun getAwaiter(player: Player): AwaitInputEffect<Boolean> {
      return AwaitInputEffect<Boolean>(
          id = "effects.awaiter.dice.rethrow",
          player = player,
          targetEffect = RethrowDiceInputEffect(player),
      )
    }
  }
}
