package ru.kislball.machikoro.triggers.utility

import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.Trigger

class BooleanTrigger(private val value: Boolean) : Trigger("triggers.boolean.$value") {
  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean = value
}
