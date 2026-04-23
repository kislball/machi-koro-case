package ru.kislball.machikoro.cli.game

import ru.kislball.machikoro.effects.dice.DiceRollInputEffect
import ru.kislball.machikoro.effects.input.InputEffect
import ru.kislball.machikoro.exceptions.CurrentStepNotReadyException
import ru.kislball.machikoro.cli.session.ReactiveGame
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.IntermediateRollResult
import ru.kislball.machikoro.game.markers.canThrowTwoDice
import ru.kislball.machikoro.game.step.FinishedStepPhase
import ru.kislball.machikoro.game.step.PendingStepPhase
import ru.kislball.machikoro.game.utilities.contains

class GameAutoAdvance {
  fun advance(game: ReactiveGame) {
    val driver = game.driver
    if (driver.game.finished) return

    while (true) {
      if (driver.game.finished) return

      val current = driver.game.currentStepPhase
      when (current) {
        null -> {
          startAndRoll(game)
          if (shouldStop(driver.game.inputEffects.peek(), driver.currentPendingOrNull())) return
        }

        is FinishedStepPhase -> {
          startAndRoll(game)
          if (shouldStop(driver.game.inputEffects.peek(), driver.currentPendingOrNull())) return
        }

        is PendingStepPhase -> {
          if (driver.game.inputEffects.peek() != null) return
          if (!current.results.contains<DiceRollResult>() &&
              !current.results.contains<IntermediateRollResult>()) {
            rollAutomatically(game, current)
            if (shouldStop(driver.game.inputEffects.peek(), driver.currentPendingOrNull())) return
            continue
          }
          return
        }
      }
    }
  }

  private fun startAndRoll(game: ReactiveGame) {
    val pending = game.driver.nextStep()
    rollAutomatically(game, pending)
  }

  private fun rollAutomatically(game: ReactiveGame, step: PendingStepPhase) {
    val numDice = if (step.currentPlayer.canThrowTwoDice()) 2 else 1
    game.driver.rollDice(step.currentPlayer, numDice)
  }

  private fun shouldStop(input: InputEffect<*>?, step: PendingStepPhase?): Boolean {
    if (input != null) return true
    if (step == null) return true
    return step.results.contains<DiceRollResult>() || step.results.contains<IntermediateRollResult>()
  }

  private fun ru.kislball.machikoro.facility.GameDriver.currentPendingOrNull(): PendingStepPhase? {
    return try {
      game.currentStepPhase as? PendingStepPhase
    } catch (_: CurrentStepNotReadyException) {
      null
    }
  }
}
