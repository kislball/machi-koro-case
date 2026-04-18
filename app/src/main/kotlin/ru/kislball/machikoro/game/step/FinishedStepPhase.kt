package ru.kislball.machikoro.game.step

import ru.kislball.machikoro.game.Game

class FinishedStepPhase : StepPhase {
  constructor(
      game: Game,
      step: StepPhase,
  ) : super(game, step.currentPlayer, step.stepNumber) {
    check(!this.game.finished) { "Game has been finished" }
    finalised = true
  }
}
