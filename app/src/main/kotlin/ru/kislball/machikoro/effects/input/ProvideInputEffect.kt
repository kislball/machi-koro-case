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
        check(fromPlayer == effect.player) { "Input is provided by non-owner player" }
        require(effect.checkInput(input)) { "Invalid input for effect ${effect.id}" }
        require(effect.isValid(stepPhase, input)) { "Input is not valid for effect ${effect.id}" }
        return true
    }

  override fun run(stepPhase: StepPhase) {
    stepPhase.game.inputEffects.dequeue(effect)
    effect.applyWithInput(stepPhase, input)
  }
}
