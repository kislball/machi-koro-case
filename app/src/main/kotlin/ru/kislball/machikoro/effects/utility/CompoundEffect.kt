package ru.kislball.machikoro.effects.utility

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.step.StepPhase

class CompoundEffect(val effects: List<Effect>) : Effect("effects.compound") {
  override fun isValid(stepPhase: StepPhase): Boolean {
    return effects.all { it.isValid(stepPhase) }
  }

  override fun run(stepPhase: StepPhase) {
    for (effect in effects) {
      effect.apply(stepPhase)
    }
  }

  companion object {
    fun combineEffects(vararg effects: Effect) = CompoundEffect(effects.asList())
  }
}
