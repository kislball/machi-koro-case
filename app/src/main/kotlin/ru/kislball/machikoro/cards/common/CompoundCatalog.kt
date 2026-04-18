package ru.kislball.machikoro.cards.common

class CompoundCatalog(cards: List<List<Card>>) : CardCatalog(cards.flatten()) {}
