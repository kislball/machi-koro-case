package ru.kislball.machikoro.gui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInput
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.FinishedStepPhase
import ru.kislball.machikoro.game.step.PendingStepPhase
import ru.kislball.machikoro.gui.AppViewModel

class GameViewModel(
    val app: AppViewModel,
    val gameId: String,
) {
  val driver = ObservableGameDriver(app.loadGame(gameId))

  enum class InCenter {
    DiceResult,
    PickPlayer,
    PickCard,
  }

  var showOnCenter = mutableStateOf(false)

  @Suppress("TooManyFunctions")
  class ObservableGameDriver(driver: GameDriver) : GameDriver(driver.game) {
    private var version by mutableStateOf(0)

    override val game: Game
      get() {
        version
        return super.game
      }

    private fun refresh() {
      version++
    }

    override fun observeEffects(observer: (Effect, PendingStepPhase) -> Unit) {
      super.observeEffects(observer)
    }

    override fun nextStep(): PendingStepPhase {
      return super.nextStep().also { refresh() }
    }

    override fun rollDice(player: Player, numDice: Int): PendingStepPhase {
      return super.rollDice(player, numDice).also { refresh() }
    }

    override fun needsRethrowDecision(player: Player): Boolean {
      version
      return super.needsRethrowDecision(player)
    }

    override fun needsRollDecision(player: Player): Boolean {
      version
      return super.needsRollDecision(player)
    }

    override fun needsPickAndChargeDecision(player: Player): Boolean {
      version
      return super.needsPickAndChargeDecision(player)
    }

    override fun needsSwapCardsDecision(player: Player): Boolean {
      version
      return super.needsSwapCardsDecision(player)
    }

    override fun needsAdditionalStepDecision(player: Player): Boolean {
      version
      return super.needsAdditionalStepDecision(player)
    }

    override fun submitRethrowDecision(player: Player, shouldRethrow: Boolean): PendingStepPhase {
      return super.submitRethrowDecision(player, shouldRethrow).also { refresh() }
    }

    override fun pickAndChargePlayer(player: Player, targetPlayer: Player): PendingStepPhase {
      return super.pickAndChargePlayer(player, targetPlayer).also { refresh() }
    }

    override fun swapCards(player: Player, input: SwapCardsInput): PendingStepPhase {
      return super.swapCards(player, input).also { refresh() }
    }

    override fun submitAdditionalStepDecision(
        player: Player,
        shouldTakeAdditionalStep: Boolean,
    ): PendingStepPhase {
      return super.submitAdditionalStepDecision(player, shouldTakeAdditionalStep).also { refresh() }
    }

    override fun buyCard(player: Player, cardId: String): FinishedStepPhase? {
      return super.buyCard(player, cardId).also { refresh() }
    }
  }
}
