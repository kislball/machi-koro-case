package ru.kislball.machikoro.actions

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInput
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInputEffect
import ru.kislball.machikoro.effects.input.ProvideInputEffect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class SwapCardsAction(player: Player, private val swapInput: SwapCardsInput) :
    PlayerAction("actions.input.swap_cards", player) {
  override fun checkValid(s: StepPhase) {
    val awaitingInput = s.game.inputEffects.peek()
    require(awaitingInput is SwapCardsInputEffect) { "Current step is not awaiting swap input" }
    require(awaitingInput.player == player) { "Only effect owner can submit swap input" }
  }

  override fun getEffect(s: StepPhase): Effect {
    checkValid(s)
    val awaitingInput = s.game.inputEffects.peek() as SwapCardsInputEffect
    return ProvideInputEffect(awaitingInput, swapInput, player)
  }
}
