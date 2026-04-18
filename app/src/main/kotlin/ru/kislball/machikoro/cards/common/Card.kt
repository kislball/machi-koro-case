package ru.kislball.machikoro.cards.common

import ru.kislball.machikoro.game.Step
import ru.kislball.machikoro.game.Triggerable

abstract class Card(
    val id: String,
    val type: CardType,
    val totalCards: Int,
) : Triggerable() {
  abstract fun getPrice(s: Step): Int

  val cardNameKey: String
    get() = "$id.name"

  val cardNameDescription
    get() = "$id.description"
}
