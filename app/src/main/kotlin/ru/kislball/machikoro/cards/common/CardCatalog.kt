package ru.kislball.machikoro.cards.common

open class CardCatalog(private val cards: List<Card>) {
  private val cardsMap = cards.associateBy { it.id }

  operator fun get(id: String) = cardsMap[id]
}
