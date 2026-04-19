package ru.kislball.machikoro.cards.standard.sights

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.order.GivePlayerAdditionalStepInputEffect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.markers.setCanRethrowDice
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.dice.PossessorDiceTrigger

class EntertainmentParkCard : Card(
    type = CardType.SIGHT,
    totalCards = 4,
    cardId = "cards.entertainment_park",
    icon = CardIcon.SPECIAL,
) {
    override fun getPrice(s: StepPhase): Int = 16

    override fun getEffect(
        s: StepPhase,
        possessor: Player?
    ): Effect {
        require(possessor != null) { "possessor must be set" }
        return GivePlayerAdditionalStepInputEffect.getAwaiter(possessor)
    }

    override fun isTriggered(
        stepPhase: StepPhase,
        possessor: Player?
    ): Boolean {
        return PossessorDiceTrigger(
            dicePredicate = { it.size == 2 && it[0] == it[1] }
        ).isTriggered(stepPhase, possessor)
    }
}
