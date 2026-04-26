package ru.kislball.machikoro.game.utilities

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.Trigger

abstract class Triggerable(val triggerableId: String) : Trigger("triggerable.$triggerableId") {
  abstract fun getEffect(s: StepPhase, possessor: Player?): Effect

  fun apply(s: StepPhase, possessor: Player?) {
    if (isTriggered(s, possessor)) getEffect(s, possessor).apply(s)
  }
}
