package ru.kislball.machikoro.game

import ru.kislball.machikoro.actions.PlayerAction

class DiceRolledStep(game: Game, currentPlayer: Player, stepNumber: Int, val dice: List<Int>) :
    Step(game, currentPlayer, stepNumber) {
  init {
    for (triggerable in game.getTriggerables()) {
      if (triggerable.isTriggered(this)) {
        triggerable.getEffect(this).apply(this)
      }
    }
  }

  fun finish(action: PlayerAction): FinishedActionStep {
    action.checkValid(this)
    return FinishedActionStep(game, this, action)
  }
}
