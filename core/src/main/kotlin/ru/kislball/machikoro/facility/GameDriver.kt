package ru.kislball.machikoro.facility

import ru.kislball.machikoro.actions.BuyCardAction
import ru.kislball.machikoro.actions.BuyCardInputAction
import ru.kislball.machikoro.actions.PickAndChargePlayerAction
import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.actions.ProvideAdditionalStepDecisionAction
import ru.kislball.machikoro.actions.ProvideRethrowDecisionAction
import ru.kislball.machikoro.actions.SwapCardsAction
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.cards.buy.BuyCardDecisionResolved
import ru.kislball.machikoro.effects.cards.buy.BuyCardInputEffect
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInput
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInputEffect
import ru.kislball.machikoro.effects.dice.DiceRollInputEffect
import ru.kislball.machikoro.effects.dice.RethrowDiceInputEffect
import ru.kislball.machikoro.effects.input.AwaitInputEffect
import ru.kislball.machikoro.effects.money.PickAndChargeUserInputEffect
import ru.kislball.machikoro.effects.order.GivePlayerAdditionalStepInputEffect
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
import ru.kislball.machikoro.game.markers.canThrowTwoDice
import ru.kislball.machikoro.game.step.FinishedStepPhase
import ru.kislball.machikoro.game.step.PendingStepPhase
import ru.kislball.machikoro.game.utilities.contains

open class GameDriver(open val game: Game) {
  open fun observeEffects(observer: (Effect, PendingStepPhase) -> Unit) {
    game.addEffectObserver { effect, stepPhase ->
      val pendingStep = stepPhase as? PendingStepPhase ?: return@addEffectObserver
      observer(effect, pendingStep)
    }
  }

  open fun nextStep(): PendingStepPhase {
    val nextStep = game.nextStep()
    check(nextStep is PendingStepPhase) {
      WaitingStepTypeMismatchException(
          expected = "PendingStepPhase", actual = nextStep::class.simpleName)
    }
    return nextStep as PendingStepPhase
  }

  open fun rollDice(player: Player, numDice: Int): PendingStepPhase {
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
    enqueueBuyCardPrompt(waitingStep)

    return waitingStep
  }

  open fun needsRethrowDecision(player: Player): Boolean {
    return currentInputMatches(player) { it is RethrowDiceInputEffect }
  }

  open fun needsRollDecision(player: Player): Boolean {
    val current = game.currentStepPhase as? PendingStepPhase ?: return false
    if (current.currentPlayer != player) return false
    if (game.inputEffects.peek() != null) return false
    if (current.results.contains<DiceRollResult>()) return false
    if (current.results.contains<IntermediateRollResult>()) return false
    return player.canThrowTwoDice()
  }

  open fun needsPickAndChargeDecision(player: Player): Boolean {
    return currentInputMatches(player) { it is PickAndChargeUserInputEffect }
  }

  open fun needsSwapCardsDecision(player: Player): Boolean {
    return currentInputMatches(player) { it is SwapCardsInputEffect }
  }

  open fun needsAdditionalStepDecision(player: Player): Boolean {
    return currentInputMatches(player) { it is GivePlayerAdditionalStepInputEffect }
  }

  open fun needsBuyCardDecision(player: Player): Boolean {
    return currentInputMatches(player) { it is BuyCardInputEffect }
  }

  open fun submitRethrowDecision(player: Player, shouldRethrow: Boolean): PendingStepPhase {
    finishStep(ProvideRethrowDecisionAction(player, shouldRethrow))
    return currentPendingStep
  }

  open fun pickAndChargePlayer(player: Player, targetPlayer: Player): PendingStepPhase {
    finishStep(PickAndChargePlayerAction(player, targetPlayer))
    return currentPendingStep
  }

  open fun swapCards(player: Player, input: SwapCardsInput): PendingStepPhase {
    finishStep(SwapCardsAction(player, input))
    return currentPendingStep
  }

  open fun submitAdditionalStepDecision(
      player: Player,
      shouldTakeAdditionalStep: Boolean,
  ): PendingStepPhase {
    finishStep(ProvideAdditionalStepDecisionAction(player, shouldTakeAdditionalStep))
    return currentPendingStep
  }

  open fun buyCard(player: Player, cardId: String): FinishedStepPhase? {
    return finishStep(BuyCardInputAction(player, cardId))
  }

  open fun skipCardPurchase(player: Player): FinishedStepPhase? {
    return finishStep(BuyCardInputAction(player, null))
  }

  private val currentPendingStep: PendingStepPhase
    get() = game.currentStepPhase as? PendingStepPhase ?: throw CurrentStepNotReadyException()

  private inline fun currentInputMatches(
      player: Player,
      matches: (Any?) -> Boolean,
  ): Boolean {
    val current = game.currentStepPhase as? PendingStepPhase ?: return false
    if (current.currentPlayer != player) return false
    return matches(game.inputEffects.peek())
  }

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
        enqueueBuyCardPrompt(current)
      }

      if (action is BuyCardInputAction && game.inputEffects.peek() == null) {
        return current.finish()
      }
      return null
    }

    val finishedStep = current.submitPlayerAction(action)
    return finishedStep
  }

  private fun enqueueBuyCardPrompt(step: PendingStepPhase) {
    if (game.finished) return
    if (!step.results.contains<DiceRollResult>()) return
    if (step.results.contains<BuyCardDecisionResolved>()) return
    if (game.inputEffects.toList().any {
      it is BuyCardInputEffect && it.player == step.currentPlayer
    }) {
      return
    }

    AwaitInputEffect(
            id = "effects.awaiter.cards.buy",
            player = step.currentPlayer,
            targetEffect = BuyCardInputEffect(step.currentPlayer),
            addToEnd = true,
        )
        .apply(step)
  }
}
