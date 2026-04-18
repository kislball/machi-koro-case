package ru.kislball.machikoro.effects

import ru.kislball.machikoro.game.step.StepPhase

class NoopEffect : Effect("effects.noop") {
  override fun apply(stepPhase: StepPhase) = Unit
}
