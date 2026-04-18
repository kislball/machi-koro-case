package ru.kislball.machikoro.effects

import ru.kislball.machikoro.game.Step

class NoopEffect : Effect() {
  override var effectDescriptionKey: String = "effect.noop.description"
  override var effectNameKey: String = "effect.noop.name"

  override fun apply(step: Step) {}
}
