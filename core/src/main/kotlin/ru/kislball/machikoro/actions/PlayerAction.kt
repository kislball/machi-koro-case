package ru.kislball.machikoro.actions

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

abstract class PlayerAction(val id: String, val player: Player) {
  abstract fun checkValid(s: StepPhase)

  abstract fun getEffect(s: StepPhase): Effect

  open val actionNameKey: String
    get() = "$id.name"

  open val actionDescriptionKey: String
    get() = "$id.description"
}
