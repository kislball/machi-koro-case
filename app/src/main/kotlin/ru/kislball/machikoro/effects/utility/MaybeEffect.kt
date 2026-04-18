package ru.kislball.machikoro.effects.utility

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.step.StepPhase

class MaybeEffect(val inner: Effect) : Effect("effects.maybe") {
  override fun apply(stepPhase: StepPhase) {
    try {
      inner.apply(stepPhase)
    } catch (e: Exception) {
      print(
          "${this.javaClass.simpleName}: effect ${inner.javaClass.simpleName} emitted error ${e.message}")
    }
  }
}
