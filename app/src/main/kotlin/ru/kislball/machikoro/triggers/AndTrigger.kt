package ru.kislball.machikoro.triggers

import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class AndTrigger(val triggers: List<Trigger>) : Trigger("triggers.and") {
  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    return triggers.all { it.isTriggered(stepPhase, possessor) }
  }
}
