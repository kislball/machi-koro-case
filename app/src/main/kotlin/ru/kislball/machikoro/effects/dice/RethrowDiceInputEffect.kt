package ru.kislball.machikoro.effects.dice

import ru.kislball.machikoro.effects.input.AwaitInputEffect
import ru.kislball.machikoro.effects.input.InputEffect
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.IntermediateRollResult
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.markers.canRethrowDice
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.game.utilities.contains
import ru.kislball.machikoro.game.utilities.get
import ru.kislball.machikoro.game.utilities.remove

class RethrowDiceInputEffect(player: Player) :
    InputEffect<Boolean>("effects.rethrow_dice", player) {
  override fun applyWithInput(stepPhase: StepPhase, input: Boolean) {
    val intermediate = stepPhase.results.get<IntermediateRollResult>().result
    if (input) {
      check(player.canRethrowDice()) { "Player ${player.name} can't rethrow dice" }
      check(!stepPhase.results.contains<DiceRollResult>()) { "Dice have already been rolled" }
      DiceRollInputEffect(player).applyWithInput(stepPhase, intermediate.diceThrown.size)
    } else {
      stepPhase.results.set(intermediate)
    }
    stepPhase.results.remove<IntermediateRollResult>()
  }

  companion object {
    fun getAwaiter(player: Player): AwaitInputEffect<Boolean> {
      return AwaitInputEffect<Boolean>(
          id = "effects.awaiter.rethrow_dice",
          player = player,
          targetEffect = RethrowDiceInputEffect(player),
      )
    }
  }
}
