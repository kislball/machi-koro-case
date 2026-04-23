@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

package ru.kislball.machikoro.cli

import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.cli.session.ActiveCliGame
import ru.kislball.machikoro.cli.storage.CLIStorage
import ru.kislball.machikoro.cli.storage.TopEntry
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.facility.GameFactory
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.triggers.special.SightsCollectedTrigger

class CLIStorageTest {
  private val tempDir = createTempDirectory("machikoro-cli-storage")

  @AfterTest
  fun tearDown() {
    tempDir.deleteRecursively()
  }

  @Test
  fun `storage saves loads lists and deletes games`() {
    val storage = CLIStorage(tempDir)
    val game = ActiveCliGame(GameFactory.createDriver(ru.kislball.machikoro.cards.standard.StandardCatalog, listOf("alice", "bob")))

    storage.save("demo", game)

    assertEquals(listOf("demo"), storage.list())
    assertEquals(2, storage.load("demo").driver.game.players.size)

    storage.delete("demo")

    assertTrue(storage.list().isEmpty())
  }

  @Test
  fun `top counts wins from winner metadata`() {
    val storage = CLIStorage(tempDir)
    val alice = Player("alice")
    val bob = Player("bob")

    storage.save(
        "finished1",
        ActiveCliGame(GameDriver(Game(StandardCatalog, listOf(alice, bob), SightsCollectedTrigger(), alice))))
    storage.save(
        "finished2",
        ActiveCliGame(GameDriver(Game(StandardCatalog, listOf(alice, bob), SightsCollectedTrigger(), alice))))
    storage.save(
        "unfinished",
        ActiveCliGame(GameFactory.createDriver(StandardCatalog, listOf("alice", "bob"))))

    assertEquals(listOf(TopEntry("alice", 2)), storage.top())
  }
}
