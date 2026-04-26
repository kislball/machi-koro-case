package ru.kislball.machikoro.cards.standard.sights

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.exceptions.PossessorNotSetException
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.markers.setCanRethrowDice
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.utility.BooleanTrigger

class TVTowerCard :
    Card(
        cardId = "cards.tv_tower", type = CardType.SIGHT, totalCards = 4, icon = CardIcon.SPECIAL) {
  private val trigger = BooleanTrigger(true)

  override fun getPrice(s: StepPhase): Int = 22

  override fun getEffect(s: StepPhase, possessor: Player?): Effect {
    val p = possessor ?: throw PossessorNotSetException()
    return object : Effect("effects.sights.tv_tower.enable_rethrow") {
      override fun run(stepPhase: StepPhase) {
        p.setCanRethrowDice(true)
      }
    }
  }

  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    return trigger.isTriggered(stepPhase, possessor)
  }
}
