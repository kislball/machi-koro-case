package ru.kislball.machikoro.effects.cards

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class RemoveCardEffect(
    val from: Player,
    val card: Card,
) : Effect("effects.cards.remove") {
  override fun isValid(stepPhase: StepPhase): Boolean {
    require(from.cards.contains(card)) { "Player does not have the card to remove" }
    return true
  }

  override fun run(stepPhase: StepPhase) {
    from.cards.remove(card)
  }
}
