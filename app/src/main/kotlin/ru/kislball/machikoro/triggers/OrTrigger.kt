package ru.kislball.machikoro.triggers

import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step

class OrTrigger(val triggers: List<Trigger>) : Trigger("triggers.or") {
  override fun isTriggered(step: Step, possessor: Player?): Boolean {
    return triggers.any { it.isTriggered(step, possessor) }
  }
}
