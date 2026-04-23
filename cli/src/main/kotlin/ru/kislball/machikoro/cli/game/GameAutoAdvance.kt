package ru.kislball.machikoro.cli.game

import ru.kislball.machikoro.cli.session.ActiveCliGame
import ru.kislball.machikoro.effects.input.InputEffect
import ru.kislball.machikoro.exceptions.CurrentStepNotReadyException
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.IntermediateRollResult
import ru.kislball.machikoro.game.markers.canThrowTwoDice
import ru.kislball.machikoro.game.step.FinishedStepPhase
import ru.kislball.machikoro.game.step.PendingStepPhase
import ru.kislball.machikoro.game.utilities.contains

class GameAutoAdvance {
  fun advance(game: ActiveCliGame) {
    val driver = game.driver
    if (driver.game.finished) return

    while (true) {
      if (driver.game.finished) return

      when (val current = driver.game.currentStepPhase) {
        null,
        is FinishedStepPhase -> {
          if (advanceFromStepBoundary(game, driver)) return
        }

        is PendingStepPhase -> if (advancePendingStep(game, driver, current)) return
      }
    }
  }

  private fun advanceFromStepBoundary(game: ActiveCliGame, driver: GameDriver): Boolean {
    startAndRoll(game)
    return shouldStop(driver.game.inputEffects.peek(), driver.currentPendingOrNull())
  }

  private fun advancePendingStep(
      game: ActiveCliGame,
      driver: GameDriver,
      current: PendingStepPhase,
  ): Boolean {
    if (driver.game.inputEffects.peek() != null) return true
    if (current.results.contains<DiceRollResult>() ||
        current.results.contains<IntermediateRollResult>()) {
      return true
    }

    rollAutomatically(game, current)
    return shouldStop(driver.game.inputEffects.peek(), driver.currentPendingOrNull())
  }

  private fun startAndRoll(game: ActiveCliGame) {
    val pending = game.driver.nextStep()
    rollAutomatically(game, pending)
  }

  private fun rollAutomatically(game: ActiveCliGame, step: PendingStepPhase) {
    val numDice = if (step.currentPlayer.canThrowTwoDice()) 2 else 1
    game.driver.rollDice(step.currentPlayer, numDice)
  }

  private fun shouldStop(input: InputEffect<*>?, step: PendingStepPhase?): Boolean {
    if (input != null) return true
    if (step == null) return true
    return step.results.contains<DiceRollResult>() ||
        step.results.contains<IntermediateRollResult>()
  }

  private fun GameDriver.currentPendingOrNull(): PendingStepPhase? {
    return try {
      game.currentStepPhase as? PendingStepPhase
    } catch (_: CurrentStepNotReadyException) {
      null
    }
  }
}
