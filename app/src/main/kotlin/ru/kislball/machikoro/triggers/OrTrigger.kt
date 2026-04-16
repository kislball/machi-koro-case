package ru.kislball.machikoro.triggers

import ru.kislball.machikoro.game.Step

class OrTrigger(val triggers: List<Trigger>) : Trigger() {
    override val triggerDescriptionKey: String
        get() = "triggers.or.description"
    override val triggerNameKey: String
        get() = "triggers.or.name"

    override fun isTriggered(step: Step): Boolean {
        return triggers.any { it.isTriggered(step) }
    }
}