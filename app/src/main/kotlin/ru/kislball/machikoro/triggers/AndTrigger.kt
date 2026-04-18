package ru.kislball.machikoro.triggers

import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step

class AndTrigger(val triggers: List<Trigger>) : Trigger("triggers.and") {
  override fun isTriggered(step: Step, possessor: Player?): Boolean {
    return triggers.all { it.isTriggered(step, possessor) }
  }
}
