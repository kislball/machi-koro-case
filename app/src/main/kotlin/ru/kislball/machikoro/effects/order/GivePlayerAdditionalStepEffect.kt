package ru.kislball.machikoro.effects.order

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class GivePlayerAdditionalStepEffect(val player: Player) : Effect("effects.additional_step") {
    override fun run(stepPhase: StepPhase) {
        stepPhase.game.orderManager.setNext(player)
    }
}
