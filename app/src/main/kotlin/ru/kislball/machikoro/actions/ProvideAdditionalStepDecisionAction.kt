package ru.kislball.machikoro.actions

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.input.ProvideInputEffect
import ru.kislball.machikoro.effects.order.GivePlayerAdditionalStepInputEffect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class ProvideAdditionalStepDecisionAction(player: Player, private val shouldTakeAdditionalStep: Boolean) :
    PlayerAction("actions.input.additional_step_decision", player) {
  override fun checkValid(s: StepPhase) {
    val awaitingInput = s.game.inputEffects.peek()
    require(awaitingInput is GivePlayerAdditionalStepInputEffect) {
      "Current step is not awaiting additional-step decision"
    }
    require(awaitingInput.player == player) {
      "Only effect owner can submit additional-step decision"
    }
  }

  override fun getEffect(s: StepPhase): Effect {
    checkValid(s)
    val awaitingInput = s.game.inputEffects.peek() as GivePlayerAdditionalStepInputEffect
    return ProvideInputEffect(awaitingInput, shouldTakeAdditionalStep, player)
  }
}
