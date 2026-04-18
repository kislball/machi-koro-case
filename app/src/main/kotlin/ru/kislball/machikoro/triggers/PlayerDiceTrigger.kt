package ru.kislball.machikoro.triggers

import ru.kislball.machikoro.game.DiceRolledStep
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step

class PlayerDiceTrigger(val player: Player, val dice: List<Int>) : Trigger("triggers.player_dice") {
  override fun isTriggered(step: Step, possessor: Player?): Boolean {
    return if (step.currentPlayer == player && step is DiceRolledStep) {
      step.dice.any { dice.contains(it) }
    } else {
      false
    }
  }
}
