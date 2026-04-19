package ru.kislball.machikoro.facility

import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.effects.dice.DiceRollInputEffect
import ru.kislball.machikoro.effects.dice.RethrowDiceInputEffect
import ru.kislball.machikoro.effects.input.ProvideInputEffect
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.IntermediateRollResult
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.utilities.contains
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
    check(!game.finished) { "Game has been finished" }
    val waitingStep =
        when (val step = game.currentStepPhase) {
          null -> nextStep()
          is PendingStepPhase -> step
          else -> error("Current step is not waiting for dice roll")
        }

    require(waitingStep.currentPlayer == player) { "Only current player can roll dice" }
    check(waitingStep.canBeFinished()) { "Step can't be finished" }
    check(!waitingStep.results.contains<DiceRollResult>()) { "Dice have already been rolled" }
    check(!waitingStep.results.contains<IntermediateRollResult>()) { "Rethrow decision is pending" }
    check(game.inputEffects.peek() == null) { "Input is pending" }

    DiceRollInputEffect(player).applyWithInput(waitingStep, numDice)

    if (game.inputEffects.peek() != null) {
      return waitingStep
    }

    waitingStep.runTriggerables()

    return waitingStep
  }

  fun needsRethrowDecision(player: Player): Boolean {
    val current = game.currentStepPhase as? PendingStepPhase ?: return false
    if (current.currentPlayer != player) return false
    return game.inputEffects.peek() is RethrowDiceInputEffect
  }

  fun submitRethrowDecision(player: Player, shouldRethrow: Boolean): PendingStepPhase {
    check(!game.finished) { "Game has been finished" }
    val current =
        game.currentStepPhase as? PendingStepPhase
            ?: error("Current step is not waiting for rethrow decision")
    require(current.currentPlayer == player) { "Only current player can submit rethrow decision" }
    check(current.canBeFinished()) { "Step can't be finished" }

    val awaitingInput =
        game.inputEffects.peek() as? RethrowDiceInputEffect
            ?: error("Current step is not awaiting rethrow decision")

    ProvideInputEffect(awaitingInput, shouldRethrow, player).apply(current)
    check(current.results.contains<DiceRollResult>()) { "Rethrow decision did not produce dice result" }
    current.runTriggerables()
    return current
  }

  fun finishStep(action: PlayerAction): FinishedStepPhase? {
    val current =
        game.currentStepPhase as? PendingStepPhase
            ?: error("Current step is not ready for player action")
    require(current.currentPlayer == action.player) { "Only current player can submit action" }
    val finishedStep = current.submitPlayerAction(action)
    if (finishedStep != null) {
      game.steps.add(finishedStep)
    }
    return finishedStep
  }
}
