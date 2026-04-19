package ru.kislball.machikoro.effects.money

import ru.kislball.machikoro.effects.input.AwaitInputEffect
import ru.kislball.machikoro.effects.input.InputEffect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class PickAndChargeUserInputEffect(val to: Player, val amount: Int) :
    InputEffect<Player>("effects.pick_and_charge_user", to) {

  override fun checkInput(input: Player): Boolean {
    // Suppression is needed since type-safety may have been violated
    // by type erasure when using InputEffect<*>
    @Suppress("USELESS_IS_CHECK") require(input is Player) { "Input must be a player" }
    require(input != player) { "Player can't pick themselves" }

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
          "awaiter.effects.pick_and_charge_user",
          player = to,
          targetEffect = PickAndChargeUserInputEffect(to, amount),
      )
    }
  }
}
