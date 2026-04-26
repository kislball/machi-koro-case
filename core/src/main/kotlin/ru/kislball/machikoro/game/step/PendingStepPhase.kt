package ru.kislball.machikoro.game.step

import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.exceptions.DiceNotRolledException
import ru.kislball.machikoro.exceptions.GameFinishedException
import ru.kislball.machikoro.exceptions.InputEffectPendingException
import ru.kislball.machikoro.exceptions.StepNotFinishableException
import ru.kislball.machikoro.exceptions.check
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.utilities.contains

class PendingStepPhase(game: Game, currentPlayer: Player, stepNumber: Int) :
    StepPhase(game, currentPlayer, stepNumber) {
  fun submitPlayerAction(action: PlayerAction): FinishedStepPhase? {
    check(!this.game.finished) { GameFinishedException() }
    check(canBeFinished()) { StepNotFinishableException() }
    check(results.contains<DiceRollResult>()) { DiceNotRolledException() }
    action.checkValid(this)

    val effect = action.getEffect(this)
    effect.apply(this)
    this.runTriggerables()

    return if (game.inputEffects.peek() == null) {
      finish()
    } else {
      null
    }
  }

  internal fun finish(): FinishedStepPhase {
    check(canBeFinished()) { StepNotFinishableException() }
    check(game.inputEffects.peek() == null) { InputEffectPendingException() }
    return substitute(FinishedStepPhase(game, this))
  }
}
