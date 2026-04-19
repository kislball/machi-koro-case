package ru.kislball.machikoro.actions

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.triggers.special.SightsCollectedTrigger

class BuyCardActionTest {
  @Test
  fun `checkValid accepts correct game state`() {
    val player = Player("p1")
    player.balance = 10
    val card = StubCard("cards.bakery")
    val game = Game(CardCatalog(card), listOf(player), SightsCollectedTrigger())
    val step = GameDriver(game).run {
      val pending = nextStep()
      rollDice(player, 1)
      pending
    }
    val action = BuyCardAction(game, player, "cards.bakery")

    action.checkValid(step)
  }

  @Test
  fun `checkValid rejects when balance is too low`() {
    val player = Player("p1")
    player.balance = 0
    val card = StubCard("cards.bakery")
    val game = Game(CardCatalog(card), listOf(player), SightsCollectedTrigger())
    val step = GameDriver(game).run {
      val pending = nextStep()
      rollDice(player, 1)
      pending
    }
    val action = BuyCardAction(game, player, "cards.bakery")

    val error = assertFailsWith<IllegalArgumentException> { action.checkValid(step) }

    assertTrue(error.message!!.contains("enough balance"))
  }

  @Test
  fun `getEffect returns and applies compound purchase effect`() {
    val player = Player("p1")
    player.balance = 10
    val card = StubCard("cards.bakery")
    val game = Game(CardCatalog(card), listOf(player), SightsCollectedTrigger())
    val step = GameDriver(game).run {
      val pending = nextStep()
      rollDice(player, 1)
      pending
    }
    val action = BuyCardAction(game, player, "cards.bakery")

    val effect = action.getEffect(step)
    effect.apply(step)

    assertEquals(9, player.balance)
    assertEquals(1, player.cards.size)
    assertEquals("cards.bakery", player.cards.first().cardId)
  }

  @Test
  fun `getEffect fails when step type is invalid`() {
    val player = Player("p1")
    player.balance = 10
    val card = StubCard("cards.bakery")
    val game = Game(CardCatalog(card), listOf(player), SightsCollectedTrigger())
    val invalidStepPhase = object : StepPhase(game, player, 1) {}
    val action = BuyCardAction(game, player, "cards.bakery")

    assertFailsWith<IllegalArgumentException> { action.getEffect(invalidStepPhase) }
  }
}
