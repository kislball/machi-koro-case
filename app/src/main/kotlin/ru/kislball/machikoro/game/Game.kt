package ru.kislball.machikoro.game

import ru.kislball.machikoro.cards.CardKind

class Game(val players: List<Player>) {
    private var stepNumber: Int = 0
    var steps = mutableListOf<Step>()

    val currentStep: Step?
        get() = steps.lastOrNull()
    val currentPlayer: Player?
        get() = currentStep?.currentPlayer

    init {
        require(players.isNotEmpty()) { "Player list must not be empty" }
    }

    fun getTriggerables(): Sequence<Triggerable> {
        return players.asSequence().flatMap { it.cards.asSequence() }
    }

    fun countCardsOfKind(kind: CardKind): Int {
        return players.asSequence().flatMap { player -> player.cards.asSequence().filter { it.kind == kind }  }.count()
    }

    fun nextStep(): Step {
        if (currentStep != null && currentStep is FinishedActionStep || currentStep == null) {
            stepNumber++
            val nextStep = WaitingDiceStep(
                game = this,
                currentPlayer = players[(stepNumber - 1) % players.size],
                stepNumber = stepNumber,
            )
            steps.add(nextStep)
            return nextStep
        } else {
            throw Exception("Step has not been finished")
        }
    }
}
