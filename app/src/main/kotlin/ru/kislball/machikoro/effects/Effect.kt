package ru.kislball.machikoro.effects

import ru.kislball.machikoro.game.Step

abstract class Effect(val id: String) {
  val effectDescriptionKey: String
    get() = "$id.description"

  val effectNameKey: String
    get() = "$id.name"

  abstract fun apply(step: Step)
}
