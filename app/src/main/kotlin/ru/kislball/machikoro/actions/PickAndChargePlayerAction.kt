package ru.kislball.machikoro.actions

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.input.ProvideInputEffect
import ru.kislball.machikoro.effects.money.PickAndChargeUserInputEffect
import ru.kislball.machikoro.exceptions.AwaitingInputEffectMismatchException
import ru.kislball.machikoro.exceptions.PlayerNotCurrentException
import ru.kislball.machikoro.exceptions.require
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class PickAndChargePlayerAction(player: Player, private val targetPlayer: Player) :
    PlayerAction("actions.input.pick_and_charge", player) {
  override fun checkValid(s: StepPhase) {
    val awaitingInput = s.game.inputEffects.peek()
    require(awaitingInput is PickAndChargeUserInputEffect) {
      AwaitingInputEffectMismatchException("pick-and-charge")
    }
    require((awaitingInput as PickAndChargeUserInputEffect).player == player) {
      PlayerNotCurrentException(player.name)
    }
  }

  override fun getEffect(s: StepPhase): Effect {
    checkValid(s)
    val awaitingInput = s.game.inputEffects.peek() as PickAndChargeUserInputEffect
    return ProvideInputEffect(awaitingInput, targetPlayer, player)
  }
}
