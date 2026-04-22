@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

package ru.kislball.machikoro.cli

import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.kislball.machikoro.facility.GameFactory

class CLIStorageTest {
  private val tempDir = createTempDirectory("machikoro-cli-storage")

  @AfterTest
  fun tearDown() {
    tempDir.deleteRecursively()
  }

  @Test
  fun `storage saves loads lists and deletes games`() {
    val storage = CLIStorage(tempDir)
    val game = ReactiveGame(GameFactory.createDriver(ru.kislball.machikoro.cards.standard.StandardCatalog, listOf("alice", "bob")))

    storage.save("demo", game)

    assertEquals(listOf("demo"), storage.list())
    assertEquals(2, storage.load("demo").driver.game.players.size)

    storage.delete("demo")

    assertTrue(storage.list().isEmpty())
  }
}
