package ru.kislball.machikoro.triggers

import ru.kislball.machikoro.game.DiceRolledStep
import ru.kislball.machikoro.game.Step

class AnyDiceTrigger(val dice: List<Int>) : Trigger() {
    override val triggerDescriptionKey: String
        get() = "triggers.any-dice.description"
    override val triggerNameKey: String
        get() = "triggers.any-dice.name"

    override fun isTriggered(step: Step): Boolean {
        return if (step is DiceRolledStep) {
            step.dice.any { dice.contains(it) }
        } else {
            false
        }
    }
}