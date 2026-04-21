package ru.kislball.machikoro.cards.standard.sights

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.exceptions.PossessorNotSetException
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.markers.setThrowTwoDice
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.utility.BooleanTrigger

class RailwayStationCard :
    Card(
        cardId = "cards.railway_station",
        type = CardType.SIGHT,
        totalCards = 4,
        icon = CardIcon.SPECIAL) {
  private val trigger = BooleanTrigger(true)

  override fun getPrice(s: StepPhase): Int = 4

  override fun getEffect(s: StepPhase, possessor: Player?): Effect {
    val p = possessor ?: throw PossessorNotSetException()
    return object : Effect("effects.sights.railway_station.enable_two_dice") {
      override fun run(stepPhase: StepPhase) {
        p.setThrowTwoDice(true)
      }
    }
  }

  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    return trigger.isTriggered(stepPhase, possessor)
  }
}
