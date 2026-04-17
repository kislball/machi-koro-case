package ru.kislball.machikoro.facility

import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.game.DiceRolledStep
import ru.kislball.machikoro.game.FinishedActionStep
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.WaitingDiceStep

class GameDriver(val game: Game) {
    fun nextStep(): WaitingDiceStep {
        val nextStep = game.nextStep()
        return nextStep as? WaitingDiceStep
            ?: throw IllegalStateException("Expected WaitingDiceStep, got ${nextStep::class.simpleName}")
    }

    fun rollDice(player: Player, numDice: Int): DiceRolledStep {
        val waitingStep = when (val step = game.currentStep) {
            null -> nextStep()
            is WaitingDiceStep -> step
            else -> throw IllegalStateException("Current step is not waiting for dice roll")
        }

        require(waitingStep.currentPlayer == player) { "Only current player can roll dice" }

        val rolledStep = waitingStep.rollDice(numDice)
        game.steps.add(rolledStep)
        return rolledStep
    }

    fun finishStep(action: PlayerAction): FinishedActionStep {
        val current = game.currentStep as? DiceRolledStep
            ?: throw IllegalStateException("Current step is not ready for player action")
        require(current.currentPlayer == action.player) { "Only current player can submit action" }
        val finishedStep = current.finish(action)
        game.steps.add(finishedStep)
        return finishedStep
    }
}
