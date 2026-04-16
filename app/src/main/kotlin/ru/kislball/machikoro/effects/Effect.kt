package ru.kislball.machikoro.effects

import ru.kislball.machikoro.game.Step

abstract class Effect {
  abstract var effectDescriptionKey: String
  abstract var effectNameKey: String

  abstract fun apply(step: Step)
}
