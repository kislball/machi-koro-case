package ru.kislball.machikoro.actions

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.cards.buy.BuyCardInputEffect
import ru.kislball.machikoro.effects.input.ProvideInputEffect
import ru.kislball.machikoro.exceptions.AwaitingInputEffectMismatchException
import ru.kislball.machikoro.exceptions.PlayerNotCurrentException
import ru.kislball.machikoro.exceptions.require
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class BuyCardInputAction(player: Player, private val cardId: String?) :
    PlayerAction("actions.input.buy_card", player) {
  override fun checkValid(s: StepPhase) {
    val awaitingInput = s.game.inputEffects.peek()
    require(awaitingInput is BuyCardInputEffect) {
      AwaitingInputEffectMismatchException("buy-card")
    }
    require((awaitingInput as BuyCardInputEffect).player == player) {
      PlayerNotCurrentException(player.name)
    }
  }

  override fun getEffect(s: StepPhase): Effect {
    checkValid(s)
    val awaitingInput = s.game.inputEffects.peek() as BuyCardInputEffect
    return ProvideInputEffect(awaitingInput, cardId, player)
  }
}
