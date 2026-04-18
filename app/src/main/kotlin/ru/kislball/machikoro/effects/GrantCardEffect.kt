package ru.kislball.machikoro.effects

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class GrantCardEffect(val player: Player, val card: Card) : Effect("effects.grant_card") {
  override fun apply(stepPhase: StepPhase) {
    player.cards.add(card)
  }
}
