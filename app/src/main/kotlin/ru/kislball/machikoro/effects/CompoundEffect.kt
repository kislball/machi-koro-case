package ru.kislball.machikoro.effects

import ru.kislball.machikoro.game.Step

class CompoundEffect(val effects: List<Effect>) : Effect("effects.compound") {
  override fun apply(step: Step) {
    for (effect in effects) {
      effect.apply(step)
    }
  }

  companion object {
    fun combineEffects(vararg effects: Effect) = CompoundEffect(effects.asList())
  }
}
