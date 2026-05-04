package ru.kislball.machikoro.triggers.dice

import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.game.utilities.getOrNull
import ru.kislball.machikoro.triggers.Trigger

class AnyDiceTrigger(private val dicePredicate: (List<Int>) -> Boolean) :
    Trigger("triggers.any-dice") {
  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    val rolled = stepPhase.results.getOrNull<DiceRollResult>() ?: return false
    return dicePredicate(rolled.diceThrown)
  }

  constructor(singleDice: Int) : this({ rolled -> rolled.sum() == singleDice })

  constructor(dice: List<Int>) : this({ rolled -> rolled.sum() in dice })
}
