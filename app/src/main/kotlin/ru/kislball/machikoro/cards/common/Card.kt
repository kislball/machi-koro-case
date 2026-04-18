package ru.kislball.machikoro.cards.common

import ru.kislball.machikoro.game.Step
import ru.kislball.machikoro.game.Triggerable

abstract class Card : Triggerable() {
  abstract val cardNameKey: String
  abstract val cardNameDescription: String
  abstract val kind: CardKind

  abstract fun getPrice(s: Step): Int
}
