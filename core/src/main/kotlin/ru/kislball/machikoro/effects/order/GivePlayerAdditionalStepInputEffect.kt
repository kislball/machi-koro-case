package ru.kislball.machikoro.effects.order

import ru.kislball.machikoro.effects.input.AwaitInputEffect
import ru.kislball.machikoro.effects.input.InputEffect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class GivePlayerAdditionalStepInputEffect(player: Player) :
    InputEffect<Boolean>(
        id = "effects.order.additional_step",
        player = player,
    ) {
  override fun applyWithInput(stepPhase: StepPhase, input: Boolean) {
    if (input) {
      GivePlayerAdditionalStepEffect(player).apply(stepPhase)
    }
  }

  companion object {
    fun getAwaiter(player: Player): AwaitInputEffect<Boolean> {
      return AwaitInputEffect<Boolean>(
          id = "effects.awaiter.order.additional_step",
          player = player,
          targetEffect = GivePlayerAdditionalStepInputEffect(player),
      )
    }
  }
}
