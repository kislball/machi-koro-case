package ru.kislball.machikoro.effects

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import ru.kislball.machikoro.CountingEffect
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.effects.cards.GrantCardEffect
import ru.kislball.machikoro.effects.money.MoneyTransferEffect
import ru.kislball.machikoro.effects.money.MoneyTransferType
import ru.kislball.machikoro.effects.utility.CompoundEffect
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
  fun `money transfer withdraws coins`() {
    val player = Player("from")
    player.balance = 5
    val game = Game(listOf(player))
    val step = game.nextStep()

    MoneyTransferEffect(player, 3, MoneyTransferType.Withdraw).apply(step)

    assertEquals(2, player.balance)
  }

  @Test
  fun `money transfer throws when sender balance is insufficient`() {
    val player = Player("from")
    player.balance = 1
    val game = Game(listOf(player))
    val step = game.nextStep()

    assertFailsWith<Exception> {
      MoneyTransferEffect(player, 2, MoneyTransferType.WithdrawExact).apply(step)
    }
  }

  @Test
  fun `grant card adds provided card to player`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val step = game.nextStep()
    val card = StubCard("cards.cafe")

    GrantCardEffect(player, card).apply(step)

    assertEquals(1, player.cards.size)
    assertEquals("cards.cafe", player.cards.first().cardId)
  }
}
