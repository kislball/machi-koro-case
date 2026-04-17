package ru.kislball.machikoro.game

import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.effects.Effect

class FinishedActionStep : Step {
  val action: PlayerAction
  val effect: Effect

  constructor(
      game: Game,
      step: DiceRolledStep,
      playerAction: PlayerAction
  ) : super(game, step.currentPlayer, step.stepNumber) {
    action = playerAction
    action.checkValid(step)
    effect = action.getEffect(step)
    effect.apply(step)
  }
}
