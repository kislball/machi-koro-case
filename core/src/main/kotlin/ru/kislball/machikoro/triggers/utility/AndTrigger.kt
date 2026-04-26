package ru.kislball.machikoro.triggers.utility

import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.Trigger

class AndTrigger(val triggers: List<Trigger>) : Trigger("triggers.and") {
  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    return triggers.all { it.isTriggered(stepPhase, possessor) }
  }
}
