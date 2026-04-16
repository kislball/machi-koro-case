package ru.kislball.machikoro.cards

object CardFactory {
    fun canCreate(kind: CardKind): Boolean {
        return CardCatalog.contains(kind) && CardCatalog.getCreator(kind) != null
    }

    fun create(kind: CardKind): Card {
        if (!CardCatalog.contains(kind)) {
            throw IllegalArgumentException("Unknown card kind: $kind")
        }

        return CardCatalog.getCreator(kind)?.invoke()
            ?: throw IllegalArgumentException("No card creator is registered for kind: $kind")
    }
}
