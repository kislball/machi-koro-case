package ru.kislball.machikoro.cards

class CardFactory(private val creators: Map<CardKind, () -> Card> = emptyMap()) {
    fun canCreate(kind: CardKind): Boolean = creators.containsKey(kind)

    fun create(kind: CardKind): Card {
        return creators[kind]?.invoke()
            ?: throw IllegalArgumentException("No card creator is registered for kind: $kind")
    }
}
