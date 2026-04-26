package ru.kislball.machikoro.actions

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInput
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInputEffect
import ru.kislball.machikoro.effects.input.ProvideInputEffect
import ru.kislball.machikoro.exceptions.AwaitingInputEffectMismatchException
import ru.kislball.machikoro.exceptions.PlayerNotCurrentException
import ru.kislball.machikoro.exceptions.require
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class SwapCardsAction(player: Player, private val swapInput: SwapCardsInput) :
    PlayerAction("actions.input.swap_cards", player) {
  override fun checkValid(s: StepPhase) {
    val awaitingInput = s.game.inputEffects.peek()
    require(awaitingInput is SwapCardsInputEffect) { AwaitingInputEffectMismatchException("swap") }
    require((awaitingInput as SwapCardsInputEffect).player == player) {
      PlayerNotCurrentException(player.name)
    }
  }

  override fun getEffect(s: StepPhase): Effect {
    checkValid(s)
    val awaitingInput = s.game.inputEffects.peek() as SwapCardsInputEffect
    return ProvideInputEffect(awaitingInput, swapInput, player)
  }
}
