package ru.kislball.machikoro.effects

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step

class GrantCardEffect(val player: Player, val card: Card) : Effect("effects.grant_card") {
  override fun apply(step: Step) {
    player.cards.add(card)
  }
}
