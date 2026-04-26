package ru.kislball.machikoro.cards.common

import ru.kislball.machikoro.exceptions.DuplicateCardsException
import ru.kislball.machikoro.exceptions.require

open class CardCatalog(cards: List<Card>) {
  constructor(vararg cards: Card) : this(cards.toList())

  private val cardsMap = cards.associateBy { it.cardId }

  init {
    require(cardsMap.size == cards.size) { DuplicateCardsException() }
  }

  operator fun get(id: String) = cardsMap[id]

  fun getCardList() = cardsMap.values.toList()

  open fun getStarterCards(): List<Card> = listOf()
}
