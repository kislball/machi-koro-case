package ru.kislball.machikoro.actions

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.cards.GrantCardEffect
import ru.kislball.machikoro.effects.money.MoneyTransferEffect
import ru.kislball.machikoro.effects.money.MoneyTransferType
import ru.kislball.machikoro.effects.utility.CompoundEffect
import ru.kislball.machikoro.exceptions.CardNotFoundException
import ru.kislball.machikoro.exceptions.DiceNotRolledException
import ru.kislball.machikoro.exceptions.InsufficientFundsException
import ru.kislball.machikoro.exceptions.NotEnoughCardsException
import ru.kislball.machikoro.exceptions.PlayerNotCurrentException
import ru.kislball.machikoro.exceptions.PlayerNotFoundException
import ru.kislball.machikoro.exceptions.require
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.game.utilities.contains

class BuyCardAction(game: Game, player: Player, id: String) :
    PlayerAction(id = "actions.buy_card", player) {
  val card = game.catalog[id] ?: throw CardNotFoundException(id)

  init {
    require(game.players.contains(player)) { PlayerNotFoundException(player.name) }
  }

  override fun checkValid(s: StepPhase) {
    require(player.balance >= card.getPrice(s)) { InsufficientFundsException(player.name) }
    require(s.results.contains<DiceRollResult>()) { DiceNotRolledException() }
    require(s.game.countCardsOfKind(card) < card.totalCards) {
      NotEnoughCardsException(card.cardId)
    }
    require(player == s.currentPlayer) { PlayerNotCurrentException(player.name) }
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
