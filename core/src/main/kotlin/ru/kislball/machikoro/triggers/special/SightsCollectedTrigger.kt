package ru.kislball.machikoro.triggers.special

import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.Trigger

class SightsCollectedTrigger : Trigger("triggers.sights_collected") {
  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    val sights = stepPhase.game.catalog.getCardList().filter { it.type == CardType.SIGHT }
    if (sights.isEmpty()) return false
    return stepPhase.game.players.any { player ->
      sights.all { sight -> player.cards.any { playerCard -> playerCard.cardId == sight.cardId } }
    }
  }
}
