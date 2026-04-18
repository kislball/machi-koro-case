package ru.kislball.machikoro.actions

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.GrantCardEffect
import ru.kislball.machikoro.effects.MoneyTransferEffect
import ru.kislball.machikoro.effects.MoneyTransferType
import ru.kislball.machikoro.effects.utility.CompoundEffect
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.DiceRolledStepPhase
import ru.kislball.machikoro.game.step.StepPhase

class BuyCardAction(game: Game, player: Player, id: String) :
    PlayerAction(id = "actions.buy_card", player) {
  val card = game.catalog[id] ?: throw IllegalArgumentException("Card $id does not exist")

  init {
    require(game.players.contains(player)) { "Player ${player.name} does not exist" }
  }

  override fun checkValid(s: StepPhase) {
    require(player.balance >= card.getPrice(s)) { "Player doesn't have enough balance" }
    require(s.game.countCardsOfKind(card) < card.totalCards) { "No more cards available" }
    require(s is DiceRolledStepPhase) { "Buying cards is only available during buying stage" }
    require(player == s.currentPlayer) { "Only current player can buy cards" }
  }

  override fun getEffect(s: StepPhase): Effect {
    checkValid(s)
    return CompoundEffect.combineEffects(
        MoneyTransferEffect(
            player = player, amount = card.getPrice(s), type = MoneyTransferType.WithdrawExact),
        GrantCardEffect(player, card),
    )
  }
}
