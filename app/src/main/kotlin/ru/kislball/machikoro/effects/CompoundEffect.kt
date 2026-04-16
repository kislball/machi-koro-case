package ru.kislball.machikoro.effects

import ru.kislball.machikoro.game.Step

class CompoundEffect(val effects: List<Effect>) : Effect() {
  override var effectDescriptionKey = "effects.compound.description"
  override var effectNameKey = "effects.name.description"

  override fun apply(step: Step) {
    for (effect in effects) {
      effect.apply(step)
    }
  }

  companion object {
    fun combineEffects(vararg effects: Effect) = effects
  }
}
