package ru.kislball.machikoro.game

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.triggers.Trigger

abstract class Triggerable : Trigger() {
  abstract fun getEffect(s: Step, possessor: Player?): Effect

  fun apply(s: Step, possessor: Player?) {
    if (isTriggered(s)) getEffect(s, possessor).apply(s)
  }
}
