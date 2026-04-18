package ru.kislball.machikoro.facility

import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.game.step.DiceRolledStepPhase
import ru.kislball.machikoro.game.step.FinishedActionStepPhase
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.WaitingDiceStepPhase

class GameDriver(val game: Game) {
  fun nextStep(): WaitingDiceStepPhase {
    val nextStep = game.nextStep()
    check(nextStep is WaitingDiceStepPhase) {
      "Expected WaitingDiceStep, got ${nextStep::class.simpleName}"
    }
    return nextStep
  }

  fun rollDice(player: Player, numDice: Int): DiceRolledStepPhase {
    val waitingStep =
        when (val step = game.currentStepPhase) {
          null -> nextStep()
          is WaitingDiceStepPhase -> step
          else -> error("Current step is not waiting for dice roll")
        }

    require(waitingStep.currentPlayer == player) { "Only current player can roll dice" }

    val rolledStep = waitingStep.rollDice(numDice)
    game.steps.add(rolledStep)
    return rolledStep
  }

  fun finishStep(action: PlayerAction): FinishedActionStepPhase {
    val current =
        game.currentStepPhase as? DiceRolledStepPhase ?: error("Current step is not ready for player action")
    require(current.currentPlayer == action.player) { "Only current player can submit action" }
    val finishedStep = current.finish(action)
    game.steps.add(finishedStep)
    return finishedStep
  }
}
