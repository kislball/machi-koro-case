package ru.kislball.machikoro.game

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.triggers.Trigger

abstract class Triggerable : Trigger() {
  abstract fun getEffect(s: Step): Effect

  fun apply(s: Step) {
    if (isTriggered(s)) getEffect(s).apply(s)
  }
}
