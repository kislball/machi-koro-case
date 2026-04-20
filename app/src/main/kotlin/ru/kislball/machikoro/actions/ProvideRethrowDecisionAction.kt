package ru.kislball.machikoro.actions

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.dice.RethrowDiceInputEffect
import ru.kislball.machikoro.effects.input.ProvideInputEffect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class ProvideRethrowDecisionAction(player: Player, private val shouldRethrow: Boolean) :
    PlayerAction("actions.input.rethrow_decision", player) {
  override fun checkValid(s: StepPhase) {
    val awaitingInput = s.game.inputEffects.peek()
    require(awaitingInput is RethrowDiceInputEffect) { "Current step is not awaiting rethrow decision" }
    require(awaitingInput.player == player) { "Only current player can submit rethrow decision" }
  }

  override fun getEffect(s: StepPhase): Effect {
    checkValid(s)
    val awaitingInput = s.game.inputEffects.peek() as RethrowDiceInputEffect
    return ProvideInputEffect(awaitingInput, shouldRethrow, player)
  }
}
