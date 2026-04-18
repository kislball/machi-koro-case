package ru.kislball.machikoro.cards.standard

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.MoneyTransferEffect
import ru.kislball.machikoro.effects.MoneyTransferType
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.dice.PossessorDiceTrigger

class MediumEnterpriseCard(
    id: String,
    icon: CardIcon,
    val price: Int,
    activationRange: List<Int>,
    val revenueFrom: CardIcon?,
    val reward: Int,
    totalCards: Int = 4,
) : Card(totalCards = totalCards, cardId = id, icon = icon, type = CardType.ENTERPRISE) {
  private val trigger = PossessorDiceTrigger(activationRange)

  private fun calculateMultiplier(player: Player): Int {
    revenueFrom ?: return 1
    return player.cards.count { it.icon == revenueFrom }
  }

  fun calculateReward(player: Player): Int {
    return calculateMultiplier(player) * reward
  }

  override fun getPrice(s: StepPhase): Int {
    return price
  }

  override fun getEffect(s: StepPhase, possessor: Player?): Effect {
    require(possessor != null) { "possessor must be set" }
    return MoneyTransferEffect(
        player = possessor,
        type = MoneyTransferType.Deposit,
        amount = calculateReward(possessor),
    )
  }

  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    return trigger.isTriggered(stepPhase, possessor)
  }
}
