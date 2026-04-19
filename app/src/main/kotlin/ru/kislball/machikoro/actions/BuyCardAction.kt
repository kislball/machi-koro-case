package ru.kislball.machikoro.actions

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.cards.GrantCardEffect
import ru.kislball.machikoro.effects.money.MoneyTransferEffect
import ru.kislball.machikoro.effects.money.MoneyTransferType
import ru.kislball.machikoro.effects.utility.CompoundEffect
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.contains
import ru.kislball.machikoro.game.step.StepPhase

class BuyCardAction(game: Game, player: Player, id: String) :
    PlayerAction(id = "actions.buy_card", player) {
  val card = game.catalog[id] ?: throw IllegalArgumentException("Card $id does not exist")

  init {
    require(game.players.contains(player)) { "Player ${player.name} does not exist" }
  }

  override fun checkValid(s: StepPhase) {
    require(player.balance >= card.getPrice(s)) { "Player doesn't have enough balance" }
    require(s.results.contains<DiceRollResult>()) {
      "Buying is only available after dice have been rolled"
    }
    require(s.game.countCardsOfKind(card) < card.totalCards) { "No more cards available" }
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
