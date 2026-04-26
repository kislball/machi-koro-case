package ru.kislball.machikoro.gui.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInput
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInputEffect
import ru.kislball.machikoro.effects.dice.RethrowDiceInputEffect
import ru.kislball.machikoro.effects.money.PickAndChargeUserInputEffect
import ru.kislball.machikoro.effects.order.GivePlayerAdditionalStepInputEffect
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.IntermediateRollResult
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.markers.canThrowTwoDice
import ru.kislball.machikoro.game.step.FinishedStepPhase
import ru.kislball.machikoro.game.step.PendingStepPhase
import ru.kislball.machikoro.game.utilities.contains
import ru.kislball.machikoro.game.utilities.getOrNull
import ru.kislball.machikoro.gui.AppViewModel
import ru.kislball.machikoro.localisation.CompoundLocaliser
import ru.kislball.machikoro.localisation.Localiser
import ru.kislball.machikoro.localisation.RussianLocaliser

class GameViewModel(
    val app: AppViewModel,
    val gameId: String,
) {
  val driver = ObservableGameDriver(app.loadGame(gameId))
  private val localiser: Localiser =
      CompoundLocaliser(RussianDesktopGameLocaliser(), RussianLocaliser())
  private var loggedDiceResultKey: Pair<Int, List<Int>>? = null

  var eventLog by mutableStateOf<List<String>>(emptyList())
    private set

  val eventLogTitle: String
    get() = localiser.localise("gui.game.log.title")

  val pendingInputMarker: String?
    get() {
      val step = currentPendingStep ?: return null
      val player = step.currentPlayer

      return when {
        shouldPromptDiceChoice ->
            localiser.localise("gui.game.log.pending.roll_choice", player)
        driver.needsRethrowDecision(player) ->
            localiser.localise("gui.game.log.pending.rethrow", player)
        driver.needsPickAndChargeDecision(player) ->
            localiser.localise("gui.game.log.pending.pick_player", player)
        driver.needsSwapCardsDecision(player) ->
            localiser.localise("gui.game.log.pending.swap", player)
        driver.needsAdditionalStepDecision(player) ->
            localiser.localise("gui.game.log.pending.additional_step", player)
        else -> null
      }
    }

  init {
    driver.observeEffects { effect: Effect, _: PendingStepPhase ->
      eventLog = eventLog + localiser.localise("gui.game.log.effect", effect)
    }
    appendDiceResultIfNeeded()
  }

  val currentPendingStep: PendingStepPhase?
    get() = driver.game.currentStepPhase as? PendingStepPhase

  val currentDiceResult: DiceRollResult?
    get() = currentPendingStep?.results?.getOrNull()

  val shouldPromptDiceChoice: Boolean
    get() {
      val step = currentPendingStep ?: return false
      return step.currentPlayer.canThrowTwoDice() &&
          driver.game.inputEffects.peek() == null &&
          !step.results.contains<DiceRollResult>() &&
          !step.results.contains<IntermediateRollResult>()
    }

  fun advanceGame() {
    if (driver.game.finished) return

    while (true) {
      if (driver.game.finished) return

      when (val current = driver.game.currentStepPhase) {
        null,
        is FinishedStepPhase -> {
          val pending = driver.nextStep()
          if (pending.currentPlayer.canThrowTwoDice()) return
          driver.rollDice(pending.currentPlayer, 1)
          return
        }

        is PendingStepPhase -> {
          if (driver.game.inputEffects.peek() != null) return
          if (current.results.contains<DiceRollResult>() ||
              current.results.contains<IntermediateRollResult>()) {
            return
          }
          if (current.currentPlayer.canThrowTwoDice()) return

          driver.rollDice(current.currentPlayer, 1)
          return
        }
      }
    }
  }

  fun submitDiceChoice(numDice: Int) {
    val step = currentPendingStep ?: return
    driver.rollDice(step.currentPlayer, numDice)
  }

  private fun appendDiceResultIfNeeded() {
    val step = currentPendingStep ?: return
    val result = step.results.getOrNull<DiceRollResult>() ?: return
    val resultKey = step.stepNumber to result.diceThrown
    if (loggedDiceResultKey == resultKey) return

    loggedDiceResultKey = resultKey
    eventLog = eventLog + localiser.localise("gui.game.log.dice.current", result)
  }

  enum class InCenter {
    DiceResult,
    PickPlayer,
    PickCard,
  }

  var showOnCenter = mutableStateOf(false)

  @Suppress("TooManyFunctions")
  inner class ObservableGameDriver(driver: GameDriver) : GameDriver(driver.game) {
    private var version by mutableStateOf(0)

    override val game: Game
      get() {
        version
        return super.game
      }

    private fun refresh() {
      version++
      appendDiceResultIfNeeded()
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
