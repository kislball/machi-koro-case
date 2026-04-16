package ru.kislball.machikoro.cards

object CardCatalog {
    fun allKinds(): List<CardKind> = CardKind.entries

    fun kindsByType(type: CardType): List<CardKind> = CardKind.entries.filter { it.type == type }

    fun contains(kind: CardKind): Boolean = kind in CardKind.entries

    private val creators: MutableMap<CardKind, () -> Card> = mutableMapOf()

    fun getCreator(kind: CardKind): (() -> Card)? = creators[kind]

    fun registerCreator(kind: CardKind, creator: () -> Card) {
        creators[kind] = creator
    }

    fun registerCreators(newCreators: Map<CardKind, () -> Card>) {
        creators.putAll(newCreators)
    }
}
