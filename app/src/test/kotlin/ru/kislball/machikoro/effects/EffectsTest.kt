package ru.kislball.machikoro.effects

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import ru.kislball.machikoro.CountingEffect
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.common.CardKind
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player

class EffectsTest {
  @Test
  fun `combineEffects creates compound effect`() {
    val first = CountingEffect()
    val second = CountingEffect()
    val game = Game(listOf(Player("p1")))
    val step = game.nextStep()

    val combined = CompoundEffect.combineEffects(first, second)
    combined.apply(step)

    assertEquals(1, first.appliedCount)
    assertEquals(1, second.appliedCount)
  }

  @Test
  fun `money transfer moves coins when balance is enough`() {
    val from = Player("from")
    val to = Player("to")
    from.balance = 5
    val game = Game(listOf(from, to))
    val step = game.nextStep()

    MoneyTransferEffect(from, to, 3).apply(step)

    assertEquals(2, from.balance)
    assertEquals(3, to.balance)
  }

  @Test
  fun `money transfer throws when sender balance is insufficient`() {
    val from = Player("from")
    from.balance = 1
    val game = Game(listOf(from))
    val step = game.nextStep()

    assertFailsWith<Exception> { MoneyTransferEffect(from, null, 2).apply(step) }
  }

  @Test
  fun `grant card adds created card to player`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val step = game.nextStep()
    CardCatalog.registerCreator(CardKind.CAFE) { StubCard(CardKind.CAFE) }

    GrantCardEffect(player, CardKind.CAFE).apply(step)

    assertEquals(1, player.cards.size)
    assertEquals(CardKind.CAFE, player.cards.first().kind)
  }
}
