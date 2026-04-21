package ru.kislball.machikoro.effects.utility

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.step.StepPhase

class NoopEffect : Effect("effects.utility.noop") {
  override fun run(stepPhase: StepPhase) = Unit
}
