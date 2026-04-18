package ru.kislball.machikoro.effects

import ru.kislball.machikoro.game.Step

class NoopEffect : Effect("effects.noop") {
  override fun apply(step: Step) {}
}
