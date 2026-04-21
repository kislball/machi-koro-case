package ru.kislball.machikoro.effects.money

import ru.kislball.machikoro.effects.input.AwaitInputEffect
import ru.kislball.machikoro.effects.input.InputEffect
import ru.kislball.machikoro.exceptions.EffectInputTypeMismatchException
import ru.kislball.machikoro.exceptions.PlayerCannotSwapWithSelfException
import ru.kislball.machikoro.exceptions.require
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class PickAndChargeUserInputEffect(val to: Player, val amount: Int) :
    InputEffect<Player>("effects.money.pick_and_charge", to) {

  override fun checkInput(input: Player): Boolean {
    @Suppress("USELESS_IS_CHECK") require(input is Player) { EffectInputTypeMismatchException(id) }
    require(input != player) { PlayerCannotSwapWithSelfException() }

    return true
  }

  override fun applyWithInput(stepPhase: StepPhase, input: Player) {
    val effect =
        FineEffect(
            from = input,
            to = to,
            amount = amount,
        )
    effect.apply(stepPhase)
  }

  companion object {
    fun getAwaiter(to: Player, amount: Int): AwaitInputEffect<Player> {
      return AwaitInputEffect(
          "effects.awaiter.money.pick_and_charge",
          player = to,
          targetEffect = PickAndChargeUserInputEffect(to, amount),
      )
    }
  }
}
