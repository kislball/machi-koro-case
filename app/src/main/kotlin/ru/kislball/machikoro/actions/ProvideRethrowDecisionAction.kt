package ru.kislball.machikoro.actions

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.dice.RethrowDiceInputEffect
import ru.kislball.machikoro.effects.input.ProvideInputEffect
import ru.kislball.machikoro.exceptions.AwaitingInputEffectMismatchException
import ru.kislball.machikoro.exceptions.PlayerNotCurrentException
import ru.kislball.machikoro.exceptions.require
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class ProvideRethrowDecisionAction(player: Player, private val shouldRethrow: Boolean) :
    PlayerAction("actions.input.rethrow_decision", player) {
  override fun checkValid(s: StepPhase) {
    val awaitingInput = s.game.inputEffects.peek()
    require(awaitingInput is RethrowDiceInputEffect) {
      AwaitingInputEffectMismatchException("rethrow")
    }
    require((awaitingInput as RethrowDiceInputEffect).player == player) {
      PlayerNotCurrentException(player.name)
    }
  }

  override fun getEffect(s: StepPhase): Effect {
    checkValid(s)
    val awaitingInput = s.game.inputEffects.peek() as RethrowDiceInputEffect
    return ProvideInputEffect(awaitingInput, shouldRethrow, player)
  }
}
