package ru.kislball.machikoro

import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.Trigger

class CountingEffect : Effect("test.effect") {
  override val effectDescriptionKey: String = "test.effect.description"
  override val effectNameKey: String = "test.effect.name"
  var appliedCount: Int = 0
  var lastStepPhase: StepPhase? = null

  override fun apply(stepPhase: StepPhase) {
    appliedCount += 1
    lastStepPhase = stepPhase
  }
}

class StubCard(
    cardId: String,
    type: CardType = CardType.ENTERPRISE,
    totalCards: Int = 4,
    private val triggered: Boolean = false,
    private val effect: Effect = CountingEffect(),
) : Card(cardId, type, totalCards) {
  override val cardNameKey: String = "test.card.name"
  override val cardDescriptionKey: String = "test.card.description"

  override fun getPrice(s: StepPhase): Int = 1

  override fun getEffect(s: StepPhase, possessor: Player?): Effect = effect

  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean = triggered
}

class StubAction(player: Player, private val effect: Effect = CountingEffect()) :
    PlayerAction("test.action", player) {
  var checkValidCalled = 0

  override fun checkValid(s: StepPhase) {
    checkValidCalled += 1
  }

  override fun getEffect(s: StepPhase): Effect = effect
}

class StubTrigger(private val triggered: Boolean) : Trigger("test.trigger") {
  override val triggerDescriptionKey: String = "test.trigger.description"
  override val triggerNameKey: String = "test.trigger.name"

  override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean = triggered
}


