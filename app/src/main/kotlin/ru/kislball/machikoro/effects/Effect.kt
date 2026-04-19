package ru.kislball.machikoro.effects

import ru.kislball.machikoro.game.step.StepPhase

abstract class Effect(val id: String) {
  open val effectDescriptionKey: String
    get() = "$id.description"

  open val effectNameKey: String
    get() = "$id.name"

  open fun isValid(stepPhase: StepPhase): Boolean = true

  protected abstract fun run(stepPhase: StepPhase)

  fun apply(stepPhase: StepPhase) {
    require(isValid(stepPhase)) { "Effect is not valid" }
    run(stepPhase)
  }
}
