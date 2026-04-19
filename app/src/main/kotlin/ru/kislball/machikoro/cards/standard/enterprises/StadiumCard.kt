package ru.kislball.machikoro.cards.standard.enterprises

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.money.FineEffect
import ru.kislball.machikoro.effects.utility.CompoundEffect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.dice.PossessorDiceTrigger

class StadiumCard :
    Card(cardId = "cards.stadium", type = CardType.ENTERPRISE, icon = CardIcon.SPECIAL) {
  override fun getPrice(s: StepPhase): Int = 6

  private val trigger = PossessorDiceTrigger(listOf(6))

  override fun getEffect(s: StepPhase, possessor: Player?): Effect {
    require(possessor != null) { "possessor must be set" }
    return CompoundEffect(
        s.game.players
            .filter { it != possessor }
            .map { FineEffect(from = it, to = possessor, amount = 2) })
  }

  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    return trigger.isTriggered(stepPhase, possessor)
  }
}
