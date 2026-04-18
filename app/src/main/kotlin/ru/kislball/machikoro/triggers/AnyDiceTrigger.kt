package ru.kislball.machikoro.triggers

import ru.kislball.machikoro.game.DiceRolledStep
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step

class AnyDiceTrigger(val dice: List<Int>) : Trigger("triggers.any_dice") {
  override fun isTriggered(step: Step, possessor: Player?): Boolean {
    return if (step is DiceRolledStep) {
      step.dice.any { dice.contains(it) }
    } else {
      false
    }
  }
}
