package ru.kislball.machikoro.actions

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.input.ProvideInputEffect
import ru.kislball.machikoro.effects.money.PickAndChargeUserInputEffect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class PickAndChargePlayerAction(player: Player, private val targetPlayer: Player) :
    PlayerAction("actions.input.pick_and_charge", player) {
  override fun checkValid(s: StepPhase) {
    val awaitingInput = s.game.inputEffects.peek()
    require(awaitingInput is PickAndChargeUserInputEffect) {
      "Current step is not awaiting pick-and-charge input"
    }
    require(awaitingInput.player == player) { "Only effect owner can choose target player" }
  }

  override fun getEffect(s: StepPhase): Effect {
    checkValid(s)
    val awaitingInput = s.game.inputEffects.peek() as PickAndChargeUserInputEffect
    return ProvideInputEffect(awaitingInput, targetPlayer, player)
  }
}


