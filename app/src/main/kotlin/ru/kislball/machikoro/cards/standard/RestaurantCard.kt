package ru.kislball.machikoro.cards.standard

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.CompoundEffect
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.MaybeEffect
import ru.kislball.machikoro.effects.MoneyTransferEffect
import ru.kislball.machikoro.effects.NoopEffect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step
import ru.kislball.machikoro.triggers.AnyDiceTrigger

class RestaurantCard(
    acitvationRange: List<Int>,
    id: String,
    totalCards: Int,
    val reward: Int,
    val price: Int,
) :
    Card(
        id = id,
        type = CardType.ENTERPRISE,
        totalCards = totalCards,
    ) {
  override fun getPrice(s: Step): Int = price

  override fun getEffect(s: Step, possessor: Player?): Effect {
    if (s.currentPlayer == possessor) return NoopEffect()
    return CompoundEffect.combineEffects(
        MoneyTransferEffect(from = null, to = possessor, amount = reward),
        MaybeEffect(MoneyTransferEffect(from = s.currentPlayer, to = null, amount = reward)),
    )
  }

  private val trigger = AnyDiceTrigger(acitvationRange)

  override val triggerDescriptionKey: String
    get() = trigger.triggerDescriptionKey

  override val triggerNameKey: String
    get() = trigger.triggerNameKey

  override fun isTriggered(step: Step, possessor: Player?): Boolean {
    return trigger.isTriggered(step, possessor)
  }
}
