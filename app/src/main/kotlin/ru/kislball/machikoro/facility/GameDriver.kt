package ru.kislball.machikoro.facility

import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.FinishedStepPhase
import ru.kislball.machikoro.game.step.PendingStepPhase

class GameDriver(val game: Game) {
  fun nextStep(): PendingStepPhase {
    val nextStep = game.nextStep()
    check(nextStep is PendingStepPhase) {
      "Expected WaitingDiceStep, got ${nextStep::class.simpleName}"
    }
    return nextStep
  }

  fun rollDice(player: Player, numDice: Int): PendingStepPhase {
    val waitingStep =
        when (val step = game.currentStepPhase) {
          null -> nextStep()
          is PendingStepPhase -> step
          else -> error("Current step is not waiting for dice roll")
        }

    require(waitingStep.currentPlayer == player) { "Only current player can roll dice" }

    return waitingStep.rollDice(numDice)
  }

  fun finishStep(action: PlayerAction): FinishedStepPhase? {
    val current =
        game.currentStepPhase as? PendingStepPhase
            ?: error("Current step is not ready for player action")
    require(current.currentPlayer == action.player) { "Only current player can submit action" }
    val finishedStep = current.finish(action)
    if (finishedStep != null) {
      game.steps.add(finishedStep)
    }
    return finishedStep
  }
}
