package ru.kislball.machikoro.game

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.game.utilities.ClassMap

class Player(val name: String) {
  var balance = 0
  var cards: MutableList<Card> = mutableListOf()
  val resources = ClassMap()
}
