package ru.kislball.machikoro.triggers

import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step

abstract class Trigger(val id: String) {
  val triggerDescriptionKey: String
    get() = "$id.description"

  val triggerNameKey: String
    get() = "$id.name"

  abstract fun isTriggered(step: Step, possessor: Player?): Boolean
}
