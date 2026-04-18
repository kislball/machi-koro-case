package ru.kislball.machikoro.game.step

import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Game

class FinishedActionStepPhase : StepPhase {
  val action: PlayerAction
  val effect: Effect

  constructor(
      game: Game,
      step: DiceRolledStepPhase,
      playerAction: PlayerAction
  ) : super(game, step.currentPlayer, step.stepNumber) {
    finalised = true
    action = playerAction
    action.checkValid(step)
    effect = action.getEffect(step)
    effect.apply(step)
  }
}
