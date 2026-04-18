package ru.kislball.machikoro.game

import ru.kislball.machikoro.actions.PlayerAction

class DiceRolledStep(game: Game, currentPlayer: Player, stepNumber: Int, val dice: List<Int>) :
    Step(game, currentPlayer, stepNumber) {
  fun finish(action: PlayerAction): FinishedActionStep {
    check(canBeFinished()) { "Game can't be finished" }
    action.checkValid(this)
    return substitute(FinishedActionStep(game, this, action))
  }
}
