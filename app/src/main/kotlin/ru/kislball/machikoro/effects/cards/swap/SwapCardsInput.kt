package ru.kislball.machikoro.effects.cards.swap

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.game.Player

class SwapCardsInput(val from: Player, val fromCard: Card, val toCard: Card) {
    fun isValid(): Boolean {
        check(from.cards.contains(fromCard)) { "Player does not have the card to remove" }
        check(fromCard.type != CardType.SIGHT) { "Sights can't be exchanged" }
        check(fromCard.icon != CardIcon.SPECIAL) { "Special cards can't be exchanged" }
        check(toCard.type != CardType.SIGHT) { "Sights can't be exchanged" }
        check(toCard.icon != CardIcon.SPECIAL) { "Special cards can't be exchanged" }
        return true
    }

    init {
        require(isValid()) { "State is invalid" }
    }
}
