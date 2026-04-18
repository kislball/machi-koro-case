package ru.kislball.machikoro.game

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.step.FinishedActionStepPhase
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.game.step.WaitingDiceStepPhase
import ru.kislball.machikoro.triggers.Trigger
import ru.kislball.machikoro.triggers.special.SightsCollectedTrigger

class Game(val catalog: CardCatalog, val players: List<Player>, val gameFinishedTrigger: Trigger) {
  constructor(players: List<Player>) : this(StandardCatalog, players, SightsCollectedTrigger())

  private var stepNumber: Int = 0
  var steps = mutableListOf<StepPhase>()
  var finished: Boolean = false
    private set

  private val finishedEffect =
      object : Effect("effects.game_finished") {
        override fun apply(stepPhase: StepPhase) {
          stepPhase.game.finished = true
        }
      }

  private val finishedTriggerable =
      object : Triggerable("triggerable.${gameFinishedTrigger.id}") {
        override fun getEffect(s: StepPhase, possessor: Player?): Effect {
          return finishedEffect
        }

        override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
          return gameFinishedTrigger.isTriggered(stepPhase, possessor)
        }
      }

  val currentStepPhase: StepPhase?
    get() = steps.lastOrNull()

  val currentPlayer: Player?
    get() = currentStepPhase?.currentPlayer

  init {
    require(players.isNotEmpty()) { "Player list must not be empty" }
  }

  fun getTriggerables(): Sequence<Pair<Triggerable, Player?>> {
    val cardTriggerables =
        players
            .asSequence()
            .flatMap { player -> player.cards.asSequence().map { it to player } }
            .map { (card, player) -> card as Triggerable to player }
    val finished = finishedTriggerable to null
    return sequenceOf(finished) + cardTriggerables
  }

  fun countCardsOfKind(id: String): Int {
    return players.asSequence().flatMap { it.cards }.count { it.cardId == id }
  }

  fun countCardsOfKind(card: Card): Int {
    return countCardsOfKind(card.cardId)
  }

  fun nextStep(): StepPhase {
    val canAdvance = currentStepPhase == null || currentStepPhase is FinishedActionStepPhase
    check(canAdvance) { "Step has not been finished" }

    val nextStep =
        WaitingDiceStepPhase(
            game = this,
            currentPlayer = players[stepNumber % players.size],
            stepNumber = stepNumber,
        )
    steps.add(nextStep)
    stepNumber++
    nextStep.activate()
    return nextStep
  }
}
