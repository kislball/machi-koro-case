package ru.kislball.machikoro.effects.utility.input

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class AwaitInputEffect<T>(
    id: String,
    val player: Player,
    private val targetEffect: InputEffect<T>
) : Effect(id) {
  override fun apply(stepPhase: StepPhase) {
    stepPhase.game.inputEffects.enqueue(targetEffect)
  }
}
