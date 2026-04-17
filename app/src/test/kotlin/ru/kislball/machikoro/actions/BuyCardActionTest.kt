package ru.kislball.machikoro.actions

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.cards.CardCatalog
import ru.kislball.machikoro.cards.CardKind
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step
import ru.kislball.machikoro.game.WaitingDiceStep

class BuyCardActionTest {
  @Test
  fun `checkValid accepts correct game state`() {
    val player = Player("p1")
    player.balance = 10
    val game = Game(listOf(player))
    val step = (game.nextStep() as WaitingDiceStep).rollDice(1)
    val action = BuyCardAction(player, CardKind.BAKERY)

    action.checkValid(step)
  }

  @Test
  fun `checkValid rejects when balance is too low`() {
    val player = Player("p1")
    player.balance = 0
    val game = Game(listOf(player))
    val step = (game.nextStep() as WaitingDiceStep).rollDice(1)
    val action = BuyCardAction(player, CardKind.BAKERY)

    val error = assertFailsWith<IllegalArgumentException> { action.checkValid(step) }

    assertTrue(error.message!!.contains("enough balance"))
  }

  @Test
  fun `getEffect returns and applies compound purchase effect`() {
    val player = Player("p1")
    player.balance = 10
    val game = Game(listOf(player))
    val step = (game.nextStep() as WaitingDiceStep).rollDice(1)
    CardCatalog.registerCreator(CardKind.BAKERY) { StubCard(CardKind.BAKERY) }
    val action = BuyCardAction(player, CardKind.BAKERY)

    val effect = action.getEffect(step)
    effect.apply(step)

    assertEquals(9, player.balance)
    assertEquals(1, player.cards.size)
    assertEquals(CardKind.BAKERY, player.cards.first().kind)
  }

  @Test
  fun `getEffect fails when step type is invalid`() {
    val player = Player("p1")
    player.balance = 10
    val game = Game(listOf(player))
    val invalidStep = object : Step(game, player, 1) {}
    val action = BuyCardAction(player, CardKind.BAKERY)

    assertFailsWith<IllegalArgumentException> { action.getEffect(invalidStep) }
  }
}
