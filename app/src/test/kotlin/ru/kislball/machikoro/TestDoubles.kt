package ru.kislball.machikoro

import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step
import ru.kislball.machikoro.triggers.Trigger

class CountingEffect : Effect("test.effect") {
  override val effectDescriptionKey: String = "test.effect.description"
  override val effectNameKey: String = "test.effect.name"
  var appliedCount: Int = 0
  var lastStep: Step? = null

  override fun apply(step: Step) {
    appliedCount += 1
    lastStep = step
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

  override fun getPrice(s: Step): Int = 1

  override fun getEffect(s: Step, possessor: Player?): Effect = effect

  override fun isTriggered(step: Step, possessor: Player?): Boolean = triggered
}

class StubAction(player: Player, private val effect: Effect = CountingEffect()) :
    PlayerAction("test.action", player) {
  var checkValidCalled = 0

  override fun checkValid(s: Step) {
    checkValidCalled += 1
  }

  override fun getEffect(s: Step): Effect = effect
}

class StubTrigger(private val triggered: Boolean) : Trigger("test.trigger") {
  override val triggerDescriptionKey: String = "test.trigger.description"
  override val triggerNameKey: String = "test.trigger.name"

  override fun isTriggered(step: Step, possessor: Player?): Boolean = triggered
}


