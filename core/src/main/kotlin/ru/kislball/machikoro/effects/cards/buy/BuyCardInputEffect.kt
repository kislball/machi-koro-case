package ru.kislball.machikoro.effects.cards.buy

import ru.kislball.machikoro.actions.BuyCardAction
import ru.kislball.machikoro.effects.input.InputEffect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class BuyCardInputEffect(player: Player) : InputEffect<String?>("effects.cards.buy", player) {
  override fun checkInput(input: String?): Boolean {
    return input == null || input.isNotBlank()
  }

  override fun isValid(stepPhase: StepPhase, input: String?): Boolean {
    if (input == null) return true
    BuyCardAction(stepPhase.game, player, input).checkValid(stepPhase)
    return true
  }

  override fun applyWithInput(stepPhase: StepPhase, input: String?) {
    if (input != null) {
      BuyCardAction(stepPhase.game, player, input).getEffect(stepPhase).apply(stepPhase)
    }
    stepPhase.results.set(BuyCardDecisionResolved())
  }
}

class BuyCardDecisionResolved
