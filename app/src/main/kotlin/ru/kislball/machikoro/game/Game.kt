package ru.kislball.machikoro.game

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.standard.StandardCatalog

class Game(val catalog: CardCatalog, val players: List<Player>) {
  constructor(players: List<Player>) : this(StandardCatalog, players)

  private var stepNumber: Int = 0
  var steps = mutableListOf<Step>()

  val currentStep: Step?
    get() = steps.lastOrNull()

  val currentPlayer: Player?
    get() = currentStep?.currentPlayer

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

  fun nextStep(): Step {
    val canAdvance = currentStep == null || currentStep is FinishedActionStep
    check(canAdvance) { "Step has not been finished" }

    stepNumber++
    val nextStep =
        WaitingDiceStep(
            game = this,
            currentPlayer = players[stepNumber % players.size],
            stepNumber = stepNumber,
        )
    steps.add(nextStep)
    return nextStep
  }
}
