package ru.kislball.machikoro.effects.utility.input

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class ProvideInputEffect<T>(
    val effect: InputEffect<T>,
    val input: T,
    val fromPlayer: Player,
) : Effect("effects.provide_input") {
  override fun apply(stepPhase: StepPhase) {
    if (effect.checkInput(input) && fromPlayer == effect.player) {
      stepPhase.game.inputEffects.dequeue(effect)
      effect.applyWithInput(stepPhase, input)
    }
  }
}
