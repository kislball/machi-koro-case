package ru.kislball.machikoro.cards.standard

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.MoneyTransferEffect
import ru.kislball.machikoro.effects.MoneyTransferType
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.dice.AnyDiceTrigger

class NatureCard(
    cardId: String,
    activationRange: List<Int>,
    val price: Int,
    val reward: Int,
) : Card(type = CardType.ENTERPRISE, cardId = cardId) {
    private val trigger = AnyDiceTrigger(activationRange)

    override fun getPrice(s: StepPhase): Int = price

    override fun getEffect(
        s: StepPhase,
        possessor: Player?
    ): Effect {
        require(possessor != null) { "possessor must not be null" }
        return MoneyTransferEffect(
            player = possessor,
            type = MoneyTransferType.Deposit,
            amount = reward,
        )
    }

    override fun isTriggered(
        stepPhase: StepPhase,
        possessor: Player?
    ): Boolean {
        return trigger.isTriggered(stepPhase, possessor)
    }
}
