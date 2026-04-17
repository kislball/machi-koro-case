package ru.kislball.machikoro.facility

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import ru.kislball.machikoro.StubAction
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.cards.CardCatalog
import ru.kislball.machikoro.cards.CardKind
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player

class GameFactoryAndDriverTest {
  @Test
  fun `createDriver builds driver with players`() {
    val driver = GameFactory.createDriver(listOf("alice", "bob"))

    assertEquals(2, driver.game.players.size)
    assertEquals("alice", driver.game.players.first().name)
  }

  @Test
  fun `createDriver validates input names`() {
    assertFailsWith<IllegalArgumentException> { GameFactory.createDriver(emptyList()) }
    assertFailsWith<IllegalArgumentException> { GameFactory.createDriver(listOf(" ")) }
    assertFailsWith<IllegalArgumentException> { GameFactory.createDriver(listOf("a", "a")) }
  }

  @Test
  fun `export delegates to provided exporter`() {
    val game = Game(listOf(Player("p1")))
    val exporter = object : GameExporter {
      override fun export(game: Game): String = "ok"
    }

    val exported = GameFactory.export(game, exporter)

    assertEquals("ok", exported)
  }

  @Test
  fun `import delegates to provided importer`() {
    val importer = object : GameImporter {
      override fun import(content: String): Game = Game(listOf(Player(content)))
    }

    val driver = GameFactory.import("alex", importer)

    assertEquals("alex", driver.game.players.single().name)
  }

  @Test
  fun `nextStep starts waiting step`() {
    val game = Game(listOf(Player("p1")))
    val driver = GameDriver(game)

    val step = driver.nextStep()

    assertEquals("p1", step.currentPlayer.name)
  }

  @Test
  fun `rollDice appends rolled step to game history`() {
    val player = Player("p1")
    val game = Game(listOf(player))
    val driver = GameDriver(game)

    val rolled = driver.rollDice(player, 1)

    assertEquals(2, game.steps.size)
    assertEquals(rolled, game.currentStep)
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
    val game = Game(listOf(player))
    val driver = GameDriver(game)
    CardCatalog.registerCreator(CardKind.BAKERY) { StubCard(CardKind.BAKERY) }
    val rolled = driver.rollDice(player, 1)
    assertNotNull(rolled)

    val finished = driver.finishStep(StubAction(player))

    assertEquals(finished, game.currentStep)
    assertTrue(game.steps.size >= 3)
  }
}

