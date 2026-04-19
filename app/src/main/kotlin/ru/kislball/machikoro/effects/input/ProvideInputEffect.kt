package ru.kislball.machikoro.effects.input

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class ProvideInputEffect<T>(
    val effect: InputEffect<T>,
    val input: T,
    val fromPlayer: Player,
) : Effect("effects.provide_input") {
    override fun isValid(stepPhase: StepPhase): Boolean {
        check(stepPhase.game.inputEffects.hasEffect(effect)) { "Effect is not awaiting input" }
        return true
    }

  override fun run(stepPhase: StepPhase) {
    if (effect.checkInput(input) && fromPlayer == effect.player) {
      stepPhase.game.inputEffects.dequeue(effect)
      effect.applyWithInput(stepPhase, input)
    }
  }
}
