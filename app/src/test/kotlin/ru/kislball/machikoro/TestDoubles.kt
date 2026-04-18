package ru.kislball.machikoro

import ru.kislball.machikoro.actions.PlayerAction
import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardKind
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step
import ru.kislball.machikoro.triggers.Trigger

class CountingEffect : Effect() {
  override var effectDescriptionKey: String = "test.effect.description"
  override var effectNameKey: String = "test.effect.name"
  var appliedCount: Int = 0
  var lastStep: Step? = null

  override fun apply(step: Step) {
    appliedCount += 1
    lastStep = step
  }
}

class StubCard(
    override val kind: CardKind,
    private val triggered: Boolean = false,
    private val effect: Effect = CountingEffect(),
) : Card() {
  override val triggerDescriptionKey: String = "test.trigger.description"
  override val triggerNameKey: String = "test.trigger.name"
  override val cardNameKey: String = "test.card.name"
  override val cardDescriptionKey: String = "test.card.description"

  override fun getPrice(s: Step): Int = kind.basePrice

  override fun getEffect(s: Step): Effect = effect

  override fun isTriggered(step: Step): Boolean = triggered
}

class StubAction(player: Player, private val effect: Effect = CountingEffect()) :
    PlayerAction(player) {
  var checkValidCalled = 0

  override fun checkValid(s: Step) {
    checkValidCalled += 1
  }

  override fun getEffect(s: Step): Effect = effect
}

class StubTrigger(private val triggered: Boolean) : Trigger() {
  override val triggerDescriptionKey: String = "test.trigger.description"
  override val triggerNameKey: String = "test.trigger.name"

  override fun isTriggered(step: Step): Boolean = triggered
}

fun createGame(vararg playerNames: String): Game = Game(playerNames.map(::Player))
