package ru.kislball.machikoro.game

import kotlin.test.Test
import kotlin.test.assertEquals
import ru.kislball.machikoro.CountingEffect
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.cards.CardKind

class TriggerableTest {
  @Test
  fun `apply invokes effect only when trigger matches`() {
    val effect = CountingEffect()
    val triggered = StubCard(CardKind.RANCH, triggered = true, effect = effect)
    val notTriggered = StubCard(CardKind.BAKERY, triggered = false, effect = effect)
    val player = Player("p1")
    val game = Game(listOf(player))
    val step = game.nextStep()

    triggered.apply(step)
    notTriggered.apply(step)

    assertEquals(1, effect.appliedCount)
  }
}
