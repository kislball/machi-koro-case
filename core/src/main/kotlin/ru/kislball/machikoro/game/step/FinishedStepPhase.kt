package ru.kislball.machikoro.game.step

import ru.kislball.machikoro.exceptions.GameFinishedException
import ru.kislball.machikoro.exceptions.check
import ru.kislball.machikoro.game.Game

class FinishedStepPhase : StepPhase {
  constructor(
      game: Game,
      step: StepPhase,
  ) : super(game, step.currentPlayer, step.stepNumber) {
    check(!this.game.finished) { GameFinishedException() }
    finalised = true
  }
}
