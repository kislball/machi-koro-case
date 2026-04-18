package ru.kislball.machikoro.game

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.game.step.FinishedActionStepPhase
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.game.step.WaitingDiceStepPhase

class Game(val catalog: CardCatalog, val players: List<Player>) {
  constructor(players: List<Player>) : this(StandardCatalog, players)

  private var stepNumber: Int = 0
  var steps = mutableListOf<StepPhase>()

  val currentStepPhase: StepPhase?
    get() = steps.lastOrNull()

  val currentPlayer: Player?
    get() = currentStepPhase?.currentPlayer

  init {
    require(players.isNotEmpty()) { "Player list must not be empty" }
  }

  fun getTriggerables(): Sequence<Pair<Triggerable, Player?>> {
    return players
        .asSequence()
        .flatMap { player -> player.cards.asSequence().map { it to player } }
        .map { (card, player) -> card as Triggerable to player }
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
