package ru.kislball.machikoro.cards.common

import ru.kislball.machikoro.exceptions.CardNotFoundException

class OverrideStarterCardsCatalog(initialCards: List<Card>, cardIds: List<String>) :
    CardCatalog(initialCards) {
  private val starters: List<Card> =
      cardIds.map {
        initialCards.find { card -> card.cardId == it } ?: throw CardNotFoundException(it)
      }

  override fun getStarterCards(): List<Card> = starters
}
