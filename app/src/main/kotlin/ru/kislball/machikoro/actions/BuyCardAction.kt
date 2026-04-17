package ru.kislball.machikoro.actions

import ru.kislball.machikoro.cards.CardKind
import ru.kislball.machikoro.effects.CompoundEffect
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.GrantCardEffect
import ru.kislball.machikoro.effects.MoneyTransferEffect
import ru.kislball.machikoro.game.DiceRolledStep
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step

class BuyCardAction(player: Player, var kind: CardKind) : PlayerAction(player) {
  override fun checkValid(s: Step) {
    require(player.balance >= kind.basePrice) { "Player doesn't have enough balance" }
    require(s.game.countCardsOfKind(kind) < kind.totalCards) { "No more cards available" }
    require(s is DiceRolledStep) { "Buying cards is only available during buying stage" }
    require(player == s.currentPlayer) { "Only current player can buy cards" }
  }

  override fun getEffect(s: Step): Effect {
    checkValid(s)
    return CompoundEffect.combineEffects(
        MoneyTransferEffect(player, null, kind.basePrice),
        GrantCardEffect(player, kind),
    )
  }
}
