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
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.exceptions.CurrentStepNotReadyException
import ru.kislball.machikoro.exceptions.DiceAlreadyRolledException
import ru.kislball.machikoro.exceptions.GameFinishedException
import ru.kislball.machikoro.exceptions.InputEffectPendingException
import ru.kislball.machikoro.exceptions.PlayerNotCurrentException
import ru.kislball.machikoro.exceptions.RethrowDecisionPendingException
import ru.kislball.machikoro.exceptions.StepNotFinishableException
import ru.kislball.machikoro.exceptions.WaitingStepTypeMismatchException
import ru.kislball.machikoro.exceptions.check
import ru.kislball.machikoro.exceptions.require
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.IntermediateRollResult
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.FinishedStepPhase
import ru.kislball.machikoro.game.step.PendingStepPhase
import ru.kislball.machikoro.game.utilities.contains

class GameDriver(val game: Game) {
  fun observeEffects(observer: (Effect, PendingStepPhase) -> Unit) {
    game.addEffectObserver { effect, stepPhase ->
      val pendingStep = stepPhase as? PendingStepPhase ?: return@addEffectObserver
      observer(effect, pendingStep)
    }
  }

  fun nextStep(): PendingStepPhase {
    val nextStep = game.nextStep()
    check(nextStep is PendingStepPhase) {
      WaitingStepTypeMismatchException(
          expected = "PendingStepPhase", actual = nextStep::class.simpleName)
    }
    return nextStep as PendingStepPhase
  }

  fun rollDice(player: Player, numDice: Int): PendingStepPhase {
    check(!game.finished) { GameFinishedException() }
    val step = game.currentStepPhase
    val waitingStep: PendingStepPhase =
        when {
          step == null -> nextStep()
          step is PendingStepPhase -> step
          else -> throw CurrentStepNotReadyException()
        }
            as PendingStepPhase

    require(waitingStep.currentPlayer == player) { PlayerNotCurrentException(player.name) }
    check(waitingStep.canBeFinished()) { StepNotFinishableException() }
    check(!waitingStep.results.contains<DiceRollResult>()) { DiceAlreadyRolledException() }
    check(!waitingStep.results.contains<IntermediateRollResult>()) {
      RethrowDecisionPendingException()
    }
    check(game.inputEffects.peek() == null) { InputEffectPendingException() }

    DiceRollInputEffect(player).getEffect(numDice).apply(waitingStep)

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
    get() = game.currentStepPhase as? PendingStepPhase ?: throw CurrentStepNotReadyException()

  internal fun finishStep(action: PlayerAction): FinishedStepPhase? {
    check(!game.finished) { GameFinishedException() }
    val current = game.currentStepPhase as? PendingStepPhase ?: throw CurrentStepNotReadyException()
    require(current.currentPlayer == action.player) {
      PlayerNotCurrentException(action.player.name)
    }

    if (game.inputEffects.peek() != null) {
      action.getEffect(current).apply(current)

      if (action is ProvideRethrowDecisionAction) {
        check(current.results.contains<DiceRollResult>()) { DiceAlreadyRolledException() }
        current.runTriggerables()
      }
      return null
    }

    val finishedStep = current.submitPlayerAction(action)
    return finishedStep
  }
}
