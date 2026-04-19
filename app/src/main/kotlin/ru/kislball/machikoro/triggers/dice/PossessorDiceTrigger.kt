package ru.kislball.machikoro.triggers.dice

import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.game.utilities.getOrNull
import ru.kislball.machikoro.triggers.Trigger

class PossessorDiceTrigger(private val dicePredicate: (List<Int>) -> Boolean) :
    Trigger("triggers.possessor_dice") {
  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    val owner = possessor ?: return false
    val rolled = stepPhase.results.getOrNull<DiceRollResult>() ?: return false
    return rolled.player == owner && dicePredicate(rolled.diceThrown)
  }

  constructor(singleDice: Int) : this({ singleDice in it })

  constructor(dice: List<Int>) : this({ rolled -> rolled.any { it in dice } })
}
