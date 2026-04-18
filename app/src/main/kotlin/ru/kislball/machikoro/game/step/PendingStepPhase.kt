package ru.kislball.machikoro.game.step

import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.effects.input.DiceRollInputEffect
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.contains

class PendingStepPhase(game: Game, currentPlayer: Player, stepNumber: Int) :
    StepPhase(game, currentPlayer, stepNumber) {
  fun rollDice(numDice: Int): PendingStepPhase {
    check(!this.game.finished) { "Game has been finished" }
    check(canBeFinished()) { "Step can't be finished" }
    check(!results.contains<DiceRollResult>()) { "Dice have already been rolled" }
    val diceRolled = (0..numDice).map { (DICE_MIN_VALUE..DICE_MAX_VALUE).random() }
    DiceRollInputEffect(currentPlayer).applyWithInput(this, diceRolled)
    runTriggerables()
    return this
  }

  fun finish(action: PlayerAction): FinishedStepPhase? {
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

  private companion object {
    const val DICE_MIN_VALUE = 0
    const val DICE_MAX_VALUE = 7
  }
}
