package ru.kislball.machikoro.triggers

import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step

abstract class Trigger(val id: String) {
  open val triggerDescriptionKey: String
    get() = "$id.description"

  open val triggerNameKey: String
    get() = "$id.name"

  abstract fun isTriggered(step: Step, possessor: Player?): Boolean
}
