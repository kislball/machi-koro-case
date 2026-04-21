package ru.kislball.machikoro.cards.standard.enterprises

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInputEffect
import ru.kislball.machikoro.exceptions.PossessorNotSetException
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.dice.PossessorDiceTrigger

class BusinessCentreCard : Card("cards.business", CardType.ENTERPRISE, 4, CardIcon.SPECIAL) {
  override fun getPrice(s: StepPhase): Int = 8

  private val trigger = PossessorDiceTrigger(8)

  override fun getEffect(s: StepPhase, possessor: Player?): Effect {
    val p = possessor ?: throw PossessorNotSetException()
    return SwapCardsInputEffect.getAwaiter(p)
  }

  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    return trigger.isTriggered(stepPhase, possessor)
  }
}
