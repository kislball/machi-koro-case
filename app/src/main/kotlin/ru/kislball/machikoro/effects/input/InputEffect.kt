package ru.kislball.machikoro.effects.input

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

abstract class InputEffect<T>(val id: String, val player: Player) {
  abstract fun applyWithInput(stepPhase: StepPhase, input: T)

  open fun checkInput(input: T) = true
  open fun isValid(stepPhase: StepPhase, input: T) = true

  fun getEffect(input: T): Effect {
    require(checkInput(input))
    return object : Effect(id) {
      override fun run(stepPhase: StepPhase) {
        applyWithInput(stepPhase, input)
      }

      override fun isValid(stepPhase: StepPhase): Boolean {
        require(checkInput(input)) { "Invalid input for effect $id" }
        return isValid(stepPhase, input)
      }
    }
  }
}
