package ru.kislball.machikoro.triggers.special

import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.game.utilities.Triggerable

class SightsCollectedTrigger : Triggerable("triggers.sights_collected") {
  override fun getEffect(s: StepPhase, possessor: Player?): Effect {
    val winner =
        findWinner(s) ?: error("SightsCollectedTrigger#getEffect called when not triggered")
    return s.game.SetWinnerEffect(winner)
  }

  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
    return findWinner(stepPhase) != null
  }

  private fun findWinner(stepPhase: StepPhase): Player? {
    val sights = stepPhase.game.catalog.getCardList().filter { it.type == CardType.SIGHT }
    if (sights.isEmpty()) return null
    return stepPhase.game.players.firstOrNull { player ->
      sights.all { sight -> player.cards.any { playerCard -> playerCard.cardId == sight.cardId } }
    }
  }
}
