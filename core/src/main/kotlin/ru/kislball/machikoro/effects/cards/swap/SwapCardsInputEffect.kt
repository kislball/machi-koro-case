package ru.kislball.machikoro.effects.cards.swap

import ru.kislball.machikoro.effects.cards.GrantCardEffect
import ru.kislball.machikoro.effects.cards.RemoveCardEffect
import ru.kislball.machikoro.effects.input.AwaitInputEffect
import ru.kislball.machikoro.effects.input.InputEffect
import ru.kislball.machikoro.effects.utility.CompoundEffect
import ru.kislball.machikoro.exceptions.PlayerCannotSwapWithSelfException
import ru.kislball.machikoro.exceptions.require
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class SwapCardsInputEffect(
    val to: Player,
) : InputEffect<SwapCardsInput>("effects.cards.swap", to) {
  override fun checkInput(input: SwapCardsInput): Boolean {
    return input.isValid()
  }

  override fun isValid(stepPhase: StepPhase, input: SwapCardsInput): Boolean {
    require(input.from != to) { PlayerCannotSwapWithSelfException() }
    getInnerEffect(input).isValid(stepPhase)
    return true
  }

  private fun getInnerEffect(input: SwapCardsInput): CompoundEffect {
    return CompoundEffect.combineEffects(
        GrantCardEffect(to, input.fromCard),
        GrantCardEffect(input.from, input.toCard),
        RemoveCardEffect(input.from, input.fromCard),
        RemoveCardEffect(to, input.toCard),
    )
  }

  override fun applyWithInput(stepPhase: StepPhase, input: SwapCardsInput) {
    getInnerEffect(input).apply(stepPhase)
  }

  companion object {
    fun getAwaiter(to: Player): AwaitInputEffect<SwapCardsInput> {
      return AwaitInputEffect(
          "effects.awaiter.cards.swap",
          player = to,
          targetEffect = SwapCardsInputEffect(to),
      )
    }
  }
}
