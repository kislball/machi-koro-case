package ru.kislball.machikoro.game

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.exceptions.EmptyPlayersListException
import ru.kislball.machikoro.exceptions.StepNotFinishableException
import ru.kislball.machikoro.exceptions.require
import ru.kislball.machikoro.game.step.FinishedStepPhase
import ru.kislball.machikoro.game.step.PendingStepPhase
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.game.utilities.ClassMap
import ru.kislball.machikoro.game.utilities.InputEffectsQueue
import ru.kislball.machikoro.game.utilities.PlayerOrderManager
import ru.kislball.machikoro.game.utilities.Triggerable
import ru.kislball.machikoro.triggers.special.SightsCollectedTrigger

class Game(
    val catalog: CardCatalog,
    players: List<Player>,
    val gameFinishedTrigger: Triggerable,
    winner: Player? = null,
) {
  constructor(players: List<Player>) : this(StandardCatalog, players, SightsCollectedTrigger())

  private var stepNumber: Int = 0

  val inputEffects = InputEffectsQueue()
  val resources = ClassMap()
  val orderManager = PlayerOrderManager(players)
  val players: List<Player>
    get() = orderManager.players

  var steps = mutableListOf<StepPhase>()
  var finished: Boolean = false
    private set
  var winner: Player? = winner
    private set
  private val effectObservers = mutableListOf<(Effect, StepPhase) -> Unit>()

  inner class SetWinnerEffect(private val player: Player) : Effect("effects.game_finished") {
    override fun run(stepPhase: StepPhase) {
      winner = player
      finished = true
    }
  }

  val currentStepPhase: StepPhase?
    get() = steps.lastOrNull()

  val currentPlayer: Player?
    get() = currentStepPhase?.currentPlayer

  init {
    require(players.isNotEmpty()) { EmptyPlayersListException() }
    if (winner != null) {
      finished = true
    }
  }

  fun getTriggerables(): Sequence<Pair<Triggerable, Player?>> {
    val cardTriggerables =
        players
            .asSequence()
            .flatMap { player -> player.cards.asSequence().map { it to player } }
            .map { (card, player) -> card as Triggerable to player }
    val finished = gameFinishedTrigger to null
    return sequenceOf(finished) + cardTriggerables
  }

  fun countCardsOfKind(id: String): Int {
    return players.asSequence().flatMap { it.cards }.count { it.cardId == id }
  }

  fun countCardsOfKind(card: Card): Int {
    return countCardsOfKind(card.cardId)
  }

  fun nextStep(): StepPhase {
    val canAdvance = currentStepPhase == null || currentStepPhase is FinishedStepPhase
    require(canAdvance) { StepNotFinishableException() }

    val nextStep =
        PendingStepPhase(
            game = this,
            currentPlayer = orderManager.next(),
            stepNumber = stepNumber,
        )
    steps.add(nextStep)
    stepNumber++
    nextStep.activate()
    return nextStep
  }

  fun addEffectObserver(observer: (Effect, StepPhase) -> Unit) {
    effectObservers.add(observer)
  }

  fun notifyEffectApplied(effect: Effect, stepPhase: StepPhase) {
    effectObservers.forEach { it(effect, stepPhase) }
  }

}
