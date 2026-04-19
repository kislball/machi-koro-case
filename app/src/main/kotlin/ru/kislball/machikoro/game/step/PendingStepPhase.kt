package ru.kislball.machikoro.game.step

import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.contains

class PendingStepPhase(game: Game, currentPlayer: Player, stepNumber: Int) :
    StepPhase(game, currentPlayer, stepNumber) {
  fun submitPlayerAction(action: PlayerAction): FinishedStepPhase? {
    check(!this.game.finished) { "Game has been finished" }
    check(canBeFinished()) { "Step phase can't be finished" }
    check(results.contains<DiceRollResult>()) { "Dice have not been rolled yet" }
    action.checkValid(this)

    val effect = action.getEffect(this)
    effect.apply(this)
    this.runTriggerables()

    return if (game.inputEffects.peek() == null) {
      substitute(FinishedStepPhase(game, this))
    } else {
      null
    }
  }
}
