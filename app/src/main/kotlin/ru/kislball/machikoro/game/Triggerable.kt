package ru.kislball.machikoro.game

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.triggers.Trigger

abstract class Triggerable(val triggerableId: String) : Trigger("triggerable.$triggerableId") {
  abstract fun getEffect(s: Step, possessor: Player?): Effect

  fun apply(s: Step, possessor: Player?) {
    if (isTriggered(s, possessor)) getEffect(s, possessor).apply(s)
  }
}
