package ru.kislball.machikoro.facility

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import ru.kislball.machikoro.StubAction
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.exceptions.GameException
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.utilities.getOrNull
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
    assertFailsWith<GameException> { GameFactory.createDriver(CardCatalog(), emptyList()) }
    assertFailsWith<GameException> { GameFactory.createDriver(CardCatalog(), listOf(" ")) }
    assertFailsWith<GameException> { GameFactory.createDriver(CardCatalog(), listOf("a", "a")) }
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

    assertFailsWith<GameException> { driver.rollDice(p2, 1) }
  }

  @Test
  fun `rollDice rejects two dice when player has no Railway Station`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val driver = GameDriver(game)

    assertFailsWith<GameException> { driver.rollDice(player, 2) }
  }

  @Test
  fun `rollDice allows two dice when player owns Railway Station`() {
    val player = Player("p1")
    player.cards.add(StandardCatalog["cards.railway_station"]!!)
    val game = Game(listOf(player))
    val driver = GameDriver(game)

    val rolled = driver.rollDice(player, 2)

    assertEquals(2, rolled.results.getOrNull<DiceRollResult>()!!.diceThrown.size)
  }

  @Test
  fun `finishStep validates current player and appends result`() {
    val player = Player("p1")
    player.balance = 5
    val card = StubCard("cards.bakery")
    val game = Game(CardCatalog(card), listOf(player), SightsCollectedTrigger())
    val driver = GameDriver(game)
    val rolled = driver.nextStep()
    rolled.results.set(DiceRollResult(player, listOf(1)))
    assertNotNull(rolled)

    val finished = driver.finishStep(StubAction(player))

    assertEquals(finished, game.currentStepPhase)
    assertEquals(1, game.steps.size)
  }

  @Test
  fun `buy card replaces current step instead of duplicating finished step`() {
    val driver = GameFactory.createDriver(StandardCatalog, listOf("p1"))
    val player = driver.game.players.single().apply { balance = 10 }

    driver.rollDice(player, 1)
    val finished = driver.buyCard(player, "cards.wheat")

    assertNotNull(finished)
    assertEquals(finished, driver.game.currentStepPhase)
    assertEquals(1, driver.game.steps.size)
  }
}
