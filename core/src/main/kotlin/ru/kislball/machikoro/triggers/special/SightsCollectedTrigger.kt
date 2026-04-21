package ru.kislball.machikoro.triggers.special

import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.Trigger

class SightsCollectedTrigger : Trigger("triggers.sights_collected") {
  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    if (possessor == null) return false
    return stepPhase.game.catalog
        .getCardList()
        .filter { it.type == CardType.SIGHT }
        .all { possessor.cards.any { playerCard -> playerCard.cardId == it.cardId } }
  }
}
