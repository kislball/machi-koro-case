package ru.kislball.machikoro.facility

import ru.kislball.machikoro.actions.BuyCardAction
import ru.kislball.machikoro.actions.PickAndChargePlayerAction
import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.actions.ProvideAdditionalStepDecisionAction
import ru.kislball.machikoro.actions.ProvideRethrowDecisionAction
import ru.kislball.machikoro.actions.SwapCardsAction
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInput
import ru.kislball.machikoro.effects.dice.DiceRollInputEffect
import ru.kislball.machikoro.effects.dice.RethrowDiceInputEffect
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.IntermediateRollResult
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.FinishedStepPhase
import ru.kislball.machikoro.game.step.PendingStepPhase
import ru.kislball.machikoro.game.utilities.contains

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
    finishStep(ProvideRethrowDecisionAction(player, shouldRethrow))
    return currentPendingStep
  }

  fun pickAndChargePlayer(player: Player, targetPlayer: Player): PendingStepPhase {
    finishStep(PickAndChargePlayerAction(player, targetPlayer))
    return currentPendingStep
  }

  fun swapCards(player: Player, input: SwapCardsInput): PendingStepPhase {
    finishStep(SwapCardsAction(player, input))
    return currentPendingStep
  }

  fun submitAdditionalStepDecision(
      player: Player,
      shouldTakeAdditionalStep: Boolean,
  ): PendingStepPhase {
    finishStep(ProvideAdditionalStepDecisionAction(player, shouldTakeAdditionalStep))
    return currentPendingStep
  }

  fun buyCard(player: Player, cardId: String): FinishedStepPhase? {
    return finishStep(BuyCardAction(game, player, cardId))
  }

  private val currentPendingStep: PendingStepPhase
    get() = game.currentStepPhase as? PendingStepPhase ?: error("Current step is not ready")

  private fun finishStep(action: PlayerAction): FinishedStepPhase? {
    check(!game.finished) { "Game has been finished" }
    val current =
        game.currentStepPhase as? PendingStepPhase
            ?: error("Current step is not ready for player action")
    require(current.currentPlayer == action.player) { "Only current player can submit action" }

    if (game.inputEffects.peek() != null) {
      action.getEffect(current).apply(current)

      if (action is ProvideRethrowDecisionAction) {
        check(current.results.contains<DiceRollResult>()) {
          "Rethrow decision did not produce dice result"
        }
        current.runTriggerables()
      }
      return null
    }

    val finishedStep = current.submitPlayerAction(action)
    if (finishedStep != null) {
      game.steps.add(finishedStep)
    }
    return finishedStep
  }
}
