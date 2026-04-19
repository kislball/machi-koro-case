package ru.kislball.machikoro.cards.standard.enterprises

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.money.FineEffect
import ru.kislball.machikoro.effects.utility.NoopEffect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.dice.AnyDiceTrigger

class RestaurantCard(
    activationRange: List<Int>,
    id: String,
    totalCards: Int = 4,
    icon: CardIcon,
    val reward: Int,
    val price: Int,
) :
    Card(
        cardId = id,
        type = CardType.ENTERPRISE,
        totalCards = totalCards,
        icon = icon,
    ) {
  override fun getPrice(s: StepPhase): Int = price

  override fun getEffect(s: StepPhase, possessor: Player?): Effect {
    if (s.currentPlayer == possessor || possessor == null) return NoopEffect()
    return FineEffect(from = s.currentPlayer, to = possessor, amount = reward)
  }

  private val trigger = AnyDiceTrigger(activationRange)

  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    return trigger.isTriggered(stepPhase, possessor)
  }
}
