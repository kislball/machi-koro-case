package ru.kislball.machikoro.triggers.dice

import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.Trigger

class PossessorDiceTrigger(
    val dice: List<Int>,
) : Trigger("triggers.possessor_dice") {
    override fun isTriggered(
        stepPhase: StepPhase,
        possessor: Player?
    ): Boolean {
        if (possessor == null) return false
        val inner = PlayerDiceTrigger(possessor, dice)
        return inner.isTriggered(stepPhase, possessor)
    }
}
