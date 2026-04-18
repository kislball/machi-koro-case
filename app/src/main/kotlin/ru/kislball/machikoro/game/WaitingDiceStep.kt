package ru.kislball.machikoro.game

class WaitingDiceStep(game: Game, currentPlayer: Player, stepNumber: Int) :
    Step(game, currentPlayer, stepNumber) {
  fun rollDice(numDice: Int): DiceRolledStep {
    check(canBeFinished()) { "Game can't be finished" }
    val diceRolled = (0..numDice).map { (DICE_MIN_VALUE..DICE_MAX_VALUE).random() }
    return substitute(DiceRolledStep(game, currentPlayer, stepNumber, diceRolled))
  }

  private companion object {
    const val DICE_MIN_VALUE = 0
    const val DICE_MAX_VALUE = 7
  }
}
