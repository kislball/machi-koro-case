package ru.kislball.machikoro.effects

import ru.kislball.machikoro.cards.common.CardFactory
import ru.kislball.machikoro.cards.common.CardKind
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step

class GrantCardEffect(val player: Player, val kind: CardKind) : Effect() {
  override var effectDescriptionKey = "effects.buy-card.description"
  override var effectNameKey = "effects.buy-card.name"

  override fun apply(step: Step) {
    player.cards.add(CardFactory.create(kind))
  }
}
