package ru.kislball.machikoro.cards.common

object CardFactory {
  fun canCreate(kind: CardKind): Boolean {
    return CardCatalog.contains(kind) && CardCatalog.getCreator(kind) != null
  }

  fun create(kind: CardKind): Card {
    require(CardCatalog.contains(kind)) { "Unknown card kind: $kind" }

    return CardCatalog.getCreator(kind)?.invoke()
        ?: throw IllegalArgumentException("No card creator is registered for kind: $kind")
  }
}
