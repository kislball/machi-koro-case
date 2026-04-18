package ru.kislball.machikoro.game

import ru.kislball.machikoro.cards.common.Card

class Player(val name: String) {
  var balance = 0
  var cards: MutableList<Card> = mutableListOf()
  val resources = ClassMap()
}
