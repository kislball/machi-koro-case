package ru.kislball.machikoro.effects.input

import ru.kislball.machikoro.effects.FineEffect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class PickAndChargeUserInputEffect(val to: Player, val amount: Int) :
    InputEffect<Player>("effects.pick_and_charge_user", to) {
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
          "awaiter.effects.pick_and_charge_user",
          player = to,
          targetEffect = PickAndChargeUserInputEffect(to, amount),
      )
    }
  }
}