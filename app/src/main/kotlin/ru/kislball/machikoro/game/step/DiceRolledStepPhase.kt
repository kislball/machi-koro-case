package ru.kislball.machikoro.game.step

import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player

class DiceRolledStepPhase(game: Game, currentPlayer: Player, stepNumber: Int, val dice: List<Int>) :
    StepPhase(game, currentPlayer, stepNumber) {
  fun finish(action: PlayerAction): FinishedActionStepPhase {
    check(!this.game.finished) { "Game has been finished" }
    check(canBeFinished()) { "Step phase can't be finished" }
    action.checkValid(this)
    return substitute(FinishedActionStepPhase(game, this, action))
  }
}
