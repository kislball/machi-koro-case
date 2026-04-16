package ru.kislball.machikoro.cards

object CardCatalog {
    fun allKinds(): List<CardKind> = CardKind.entries

    fun kindsByType(type: CardType): List<CardKind> = CardKind.entries.filter { it.type == type }

    fun contains(kind: CardKind): Boolean = kind in CardKind.entries
}
