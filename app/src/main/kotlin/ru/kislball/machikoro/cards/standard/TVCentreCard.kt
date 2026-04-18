package ru.kislball.machikoro.cards.standard

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.input.PickAndChargeUserInputEffect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.dice.PossessorDiceTrigger

class TVCentreCard :
    Card(cardId = "cards.tv", type = CardType.ENTERPRISE, icon = CardIcon.SPECIAL) {
  override fun getPrice(s: StepPhase): Int = 7

  private val trigger = PossessorDiceTrigger(listOf(7))

  override fun getEffect(s: StepPhase, possessor: Player?): Effect {
    require(possessor != null) { "possessor must be set" }
    return PickAndChargeUserInputEffect.getAwaiter(possessor, 5)
  }

  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    return trigger.isTriggered(stepPhase, possessor)
  }
}