package ru.kislball.machikoro.cards.common

import ru.kislball.machikoro.exceptions.DuplicateCardsException
import ru.kislball.machikoro.exceptions.require

open class CardCatalog(vararg cards: Card) {
  private val cardsMap = cards.associateBy { it.cardId }

  init {
    require(cardsMap.size == cards.size) { DuplicateCardsException() }
  }

  operator fun get(id: String) = cardsMap[id]

  fun getCardList() = cardsMap.values.toList()
}
