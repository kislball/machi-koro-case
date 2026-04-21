package ru.kislball.machikoro.cards.standard.enterprises

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.money.MoneyTransferEffect
import ru.kislball.machikoro.effects.money.MoneyTransferType
import ru.kislball.machikoro.exceptions.PossessorNotSetException
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.markers.getBonusForType
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.dice.AnyDiceTrigger

class NatureCard(
    id: String,
    activationRange: List<Int>,
    icon: CardIcon,
    val price: Int,
    val reward: Int,
    totalCards: Int = 4,
) : Card(totalCards = totalCards, type = CardType.ENTERPRISE, cardId = id, icon = icon) {
  private val trigger = AnyDiceTrigger(activationRange)

  override fun getPrice(s: StepPhase): Int = price

  fun calculateReward(player: Player): Int {
    return reward + player.getBonusForType(icon)
  }

  override fun getEffect(s: StepPhase, possessor: Player?): Effect {
    val p = possessor ?: throw PossessorNotSetException()
    return MoneyTransferEffect(
        player = p,
        type = MoneyTransferType.Deposit,
        amount = calculateReward(p),
    )
  }

  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    return trigger.isTriggered(stepPhase, possessor)
  }
}
