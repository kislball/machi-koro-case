package ru.kislball.machikoro.effects.cards.swap

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.exceptions.PlayerDoesNotHaveCardException
import ru.kislball.machikoro.exceptions.SightsCannotBeExchangedException
import ru.kislball.machikoro.exceptions.SpecialCardsCannotBeExchangedException
import ru.kislball.machikoro.exceptions.check
import ru.kislball.machikoro.exceptions.require
import ru.kislball.machikoro.game.Player

class SwapCardsInput(val from: Player, val fromCard: Card, val toCard: Card) {
  fun isValid(): Boolean {
    check(from.cards.contains(fromCard)) {
      PlayerDoesNotHaveCardException(from.name, fromCard.cardId)
    }
    check(fromCard.type != CardType.SIGHT) { SightsCannotBeExchangedException() }
    check(fromCard.icon != CardIcon.SPECIAL) { SpecialCardsCannotBeExchangedException() }
    check(toCard.type != CardType.SIGHT) { SightsCannotBeExchangedException() }
    check(toCard.icon != CardIcon.SPECIAL) { SpecialCardsCannotBeExchangedException() }
    return true
  }

  init {
    require(isValid()) { SightsCannotBeExchangedException() }
  }
}
