package ru.kislball.machikoro.triggers

import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class OrTrigger(val triggers: List<Trigger>) : Trigger("triggers.or") {
  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    return triggers.any { it.isTriggered(stepPhase, possessor) }
  }
}
