package ru.kislball.machikoro.effects.input

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.exceptions.EffectInputNotValidException
import ru.kislball.machikoro.exceptions.EffectNotAwaitingInputException
import ru.kislball.machikoro.exceptions.InputProvidedByNonOwnerException
import ru.kislball.machikoro.exceptions.check
import ru.kislball.machikoro.exceptions.require
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class ProvideInputEffect<T>(
    val effect: InputEffect<T>,
    val input: T,
    val fromPlayer: Player,
) : Effect("effects.provide_input") {
  override fun isValid(stepPhase: StepPhase): Boolean {
    check(stepPhase.game.inputEffects.hasEffect(effect)) {
      EffectNotAwaitingInputException(effect.id)
    }
    check(fromPlayer == effect.player) { InputProvidedByNonOwnerException() }
    require(effect.checkInput(input)) { EffectInputNotValidException(effect.id, input.toString()) }
    require(effect.isValid(stepPhase, input)) {
      EffectInputNotValidException(effect.id, input.toString())
    }
    return true
  }

  override fun run(stepPhase: StepPhase) {
    stepPhase.game.inputEffects.dequeue(effect)
    effect.applyWithInput(stepPhase, input)
  }
}
