package ru.kislball.machikoro.facility

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.IntermediateRollResult
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.getOrNull
import ru.kislball.machikoro.game.markers.setCanRethrowDice

class GameDriverRethrowTest {
  @Test
  fun `needsRethrowDecision is true after roll when player can rethrow`() {
    val player = Player("p1")
    player.setCanRethrowDice(true)
    val game = Game(listOf(player))
    val driver = GameDriver(game)

    val rolled = driver.rollDice(player, 1)

    assertNull(rolled.results.getOrNull<DiceRollResult>())
    assertNotNull(rolled.results.getOrNull<IntermediateRollResult>())
    assertTrue(driver.needsRethrowDecision(player))
  }

  @Test
  fun `submitRethrowDecision produces final dice result`() {
    val player = Player("p1")
    player.setCanRethrowDice(true)
    val game = Game(listOf(player))
    val driver = GameDriver(game)

    val rolled = driver.rollDice(player, 1)
    val updated = driver.submitRethrowDecision(player, true)

    assertEquals(rolled, updated)
    assertNotNull(updated.results.getOrNull<DiceRollResult>())
    assertNull(updated.results.getOrNull<IntermediateRollResult>())
    assertFalse(driver.needsRethrowDecision(player))
  }

  @Test
  fun `rollDice rejects second roll while rethrow decision is pending`() {
    val player = Player("p1")
    player.setCanRethrowDice(true)
    val game = Game(listOf(player))
    val driver = GameDriver(game)
    driver.rollDice(player, 2)

    assertFailsWith<IllegalStateException> { driver.rollDice(player, 1) }
  }
}

