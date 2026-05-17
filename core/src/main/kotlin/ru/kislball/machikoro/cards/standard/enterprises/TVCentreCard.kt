package ru.kislball.machikoro.cards.standard.enterprises

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.money.PickAndChargeUserInputEffect
import ru.kislball.machikoro.exceptions.PossessorNotSetException
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.dice.PossessorDiceTrigger

class TVCentreCard :
    Card(cardId = "cards.tv", type = CardType.ENTERPRISE, icon = CardIcon.SPECIAL) {
  override fun getPrice(s: StepPhase): Int = 7

  override fun canPurchase(player: Player): Boolean {
    return player.cards.none { it.cardId == cardId }
  }

  private val trigger = PossessorDiceTrigger(listOf(7))

  override fun getEffect(s: StepPhase, possessor: Player?): Effect {
    val p = possessor ?: throw PossessorNotSetException()
    return PickAndChargeUserInputEffect.getAwaiter(p, 5)
  }

  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    return trigger.isTriggered(stepPhase, possessor)
  }
}
