package ru.kislball.machikoro.triggers

import ru.kislball.machikoro.game.step.DiceRolledStepPhase
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class AnyDiceTrigger(val dice: List<Int>) : Trigger("triggers.any-dice") {
  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    return if (stepPhase is DiceRolledStepPhase) {
      stepPhase.dice.any { dice.contains(it) }
    } else {
      false
    }
  }
}
