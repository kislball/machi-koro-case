package ru.kislball.machikoro.game

class WaitingDiceStep(game: Game, currentPlayer: Player, stepNumber: Int) :
    Step(game, currentPlayer, stepNumber) {
  fun rollDice(numDice: Int): DiceRolledStep {
    val diceRolled = (0..numDice).map { (0..7).random() }
    return DiceRolledStep(game, currentPlayer, stepNumber, diceRolled)
  }
}
