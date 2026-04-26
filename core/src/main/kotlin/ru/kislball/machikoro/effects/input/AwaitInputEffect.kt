package ru.kislball.machikoro.effects.input

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class AwaitInputEffect<T>(
    id: String,
    val player: Player,
    private val targetEffect: InputEffect<T>,
    private val addToEnd: Boolean = false,
) : Effect(id) {
  override fun run(stepPhase: StepPhase) {
    if (addToEnd) {
      stepPhase.game.inputEffects.addToEnd(targetEffect)
    } else {
      stepPhase.game.inputEffects.enqueue(targetEffect)
    }
  }
}
