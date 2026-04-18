package ru.kislball.machikoro.cards.common

open class CardCatalog(vararg cards: Card) {
  private val cardsMap = cards.associateBy { it.id }

  init {
    require(cardsMap.size == cards.size) { "Duplicate cards were found" }
  }

  operator fun get(id: String) = cardsMap[id]
}
