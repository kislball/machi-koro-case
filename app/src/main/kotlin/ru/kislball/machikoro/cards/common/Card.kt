package ru.kislball.machikoro.cards.common

import ru.kislball.machikoro.game.Step
import ru.kislball.machikoro.game.Triggerable

abstract class Card(
    val cardId: String,
    val type: CardType,
    val totalCards: Int,
) : Triggerable("card.$cardId") {
  abstract fun getPrice(s: Step): Int

  open val cardNameKey: String
    get() = "$cardId.name"

  open val cardDescriptionKey
    get() = "$cardId.description"
}
