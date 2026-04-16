package ru.kislball.machikoro.game

import ru.kislball.machikoro.cards.Card

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

    fun nextStep(): Step {
        if (currentStep != null && currentStep is FinishedActionStep || currentStep == null) {
            stepNumber++
            val nextStep = Step(
                game = this,
                currentPlayer = players[stepNumber % players.size],
                stepNumber = stepNumber,
            )
            return nextStep
        } else {
            throw Exception("Step has not been finished")
        }
    }
}