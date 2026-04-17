package ru.kislball.machikoro.triggers

import ru.kislball.machikoro.game.Step

abstract class Trigger {
  abstract val triggerDescriptionKey: String
  abstract val triggerNameKey: String

  abstract fun isTriggered(step: Step): Boolean
}
