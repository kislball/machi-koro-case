package ru.kislball.machikoro.facility

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import ru.kislball.machikoro.StubAction
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.getOrNull
import ru.kislball.machikoro.triggers.special.SightsCollectedTrigger

class GameFactoryAndDriverTest {
  @Test
  fun `createDriver builds driver with players`() {
    val driver = GameFactory.createDriver(CardCatalog(), listOf("alice", "bob"))

    assertEquals(2, driver.game.players.size)
    assertEquals("alice", driver.game.players.first().name)
  }

  @Test
  fun `createDriver validates input names`() {
    assertFailsWith<IllegalArgumentException> {
      GameFactory.createDriver(CardCatalog(), emptyList())
    }
    assertFailsWith<IllegalArgumentException> {
      GameFactory.createDriver(CardCatalog(), listOf(" "))
    }
    assertFailsWith<IllegalArgumentException> {
      GameFactory.createDriver(CardCatalog(), listOf("a", "a"))
    }
  }

  @Test
  fun `nextStep starts waiting step`() {
    val game = Game(listOf(Player("p1")))
    val driver = GameDriver(game)

    val step = driver.nextStep()

    assertEquals("p1", step.currentPlayer.name)
  }

  @Test
  fun `rollDice stores result in current waiting step`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val driver = GameDriver(game)

    val rolled = driver.rollDice(player, 1)

    assertEquals(1, game.steps.size)
    assertEquals(rolled, game.currentStepPhase)
    assertNotNull(rolled.results.getOrNull<DiceRollResult>())
  }

  @Test
  fun `rollDice rejects non current player`() {
    val p1 = Player("p1")
    val p2 = Player("p2")
    val game = Game(listOf(p1, p2))
    val driver = GameDriver(game)

    assertFailsWith<IllegalArgumentException> { driver.rollDice(p2, 1) }
  }

  @Test
  fun `finishStep validates current player and appends result`() {
    val player = Player("p1")
    player.balance = 5
    val card = StubCard("cards.bakery")
    val game = Game(CardCatalog(card), listOf(player), SightsCollectedTrigger())
    val driver = GameDriver(game)
    val rolled = driver.rollDice(player, 1)
    assertNotNull(rolled)

    val finished = driver.finishStep(StubAction(player))

    assertEquals(finished, game.currentStepPhase)
    assertTrue(game.steps.size >= 2)
  }

}
