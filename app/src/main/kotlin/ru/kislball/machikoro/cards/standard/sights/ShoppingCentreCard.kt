package ru.kislball.machikoro.cards.standard.sights

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.markers.setBonusForType
import ru.kislball.machikoro.game.step.StepPhase

class ShoppingCentreCard : Card(
    cardId = "cards.shopping_centre",
    type = CardType.SIGHT,
    icon = CardIcon.SPECIAL,
    totalCards = 4,
) {
    override fun getPrice(s: StepPhase): Int = 10

    override fun getEffect(
        s: StepPhase,
        possessor: Player?
    ): Effect {
        require(possessor != null) { "possessor must be set" }
        return object : Effect("effects.sights.shopping_centre.revenue_bonus") {
            override fun run(stepPhase: StepPhase) {
                possessor.setBonusForType(CardIcon.CAFE, 1)
                possessor.setBonusForType(CardIcon.SHOP, 1)
            }
        }
    }

    override fun isTriggered(
        stepPhase: StepPhase,
        possessor: Player?
    ): Boolean {
        return true
    }
}
