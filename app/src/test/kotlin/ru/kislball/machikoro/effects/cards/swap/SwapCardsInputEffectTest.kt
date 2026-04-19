package ru.kislball.machikoro.effects.cards.swap

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player

class SwapCardsInputEffectTest {
  @Test
  fun `swap cards exchanges cards between players`() {
    val from = Player("from")
    val to = Player("to")
    val fromCard = StubCard("cards.from")
    val toCard = StubCard("cards.to")
    from.cards.add(fromCard)
    to.cards.add(toCard)

    val game = Game(listOf(from, to))
    val step = game.nextStep()
    val input = SwapCardsInput(from, fromCard, toCard)

    SwapCardsInputEffect(to).getEffect(input).apply(step)

    assertEquals(listOf<Card>(toCard), from.cards)
    assertEquals(listOf<Card>(fromCard), to.cards)
  }

  @Test
  fun `swap cards rejects same source and target player`() {
    val player = Player("p1")
    val fromCard = StubCard("cards.from")
    val toCard = StubCard("cards.to")
    player.cards.add(fromCard)
    player.cards.add(toCard)

    val game = Game(listOf(player))
    val step = game.nextStep()
    val input = SwapCardsInput(player, fromCard, toCard)

    assertFailsWith<IllegalArgumentException> {
      SwapCardsInputEffect(player).getEffect(input).apply(step)
    }
  }

  @Test
  fun `swap cards rejects missing target card`() {
    val from = Player("from")
    val to = Player("to")
    val fromCard = StubCard("cards.from")
    val toCard = StubCard("cards.to")
    from.cards.add(fromCard)

    val game = Game(listOf(from, to))
    val step = game.nextStep()
    val input = SwapCardsInput(from, fromCard, toCard)

    assertFailsWith<IllegalArgumentException> {
      SwapCardsInputEffect(to).getEffect(input).apply(step)
    }
  }
}


