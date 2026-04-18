package ru.kislball.machikoro.game.step

import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player

class WaitingDiceStepPhase(game: Game, currentPlayer: Player, stepNumber: Int) :
    StepPhase(game, currentPlayer, stepNumber) {
  fun rollDice(numDice: Int): DiceRolledStepPhase {
    check(canBeFinished()) { "Game can't be finished" }
    val diceRolled = (0..numDice).map { (DICE_MIN_VALUE..DICE_MAX_VALUE).random() }
    return substitute(DiceRolledStepPhase(game, currentPlayer, stepNumber, diceRolled))
  }

  private companion object {
    const val DICE_MIN_VALUE = 0
    const val DICE_MAX_VALUE = 7
  }
}