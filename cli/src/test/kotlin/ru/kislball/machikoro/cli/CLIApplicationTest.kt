@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

package ru.kislball.machikoro.cli

import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CLIApplicationTest {
  private val tempDir = createTempDirectory("machikoro-cli-app")

  @AfterTest
  fun tearDown() {
    tempDir.deleteRecursively()
  }

  @Test
  fun `start command creates a game and info prints player state`() {
    val io = FakeIO(mutableListOf("alice,bob"))
    val app = CLIApplication(io, CLIStorage(tempDir))

    app.execute("start")
    app.execute("info")

    assertTrue(io.output.any { it.contains("Игрок") })
    assertTrue(io.output.any { it.contains("Кости:") })
  }

  @Test
  fun `exit in game mode returns to management mode`() {
    val io = FakeIO(mutableListOf("alice,bob"))
    val app = CLIApplication(io, CLIStorage(tempDir))

    app.execute("start")
    app.execute("exit")
    app.execute("list")

    assertTrue(io.output.any { it.contains("Выход в режим управления") })
    assertTrue(io.output.any { it.contains("Нет сохранённых игр") })
  }

  @Test
  fun `effects output does not contain raw effect ids`() {
    val io = FakeIO(mutableListOf("alice,bob"))
    val app = CLIApplication(io, CLIStorage(tempDir))

    app.execute("start")
    app.execute("buyCard cards.wheat")

    assertFalse(io.output.any { it.contains("effects.") })
  }

  private class FakeIO(
      private val input: MutableList<String>,
  ) : CLIIO {
    val output = mutableListOf<String>()

    override fun readLine(): String? = input.removeFirstOrNull()

    override fun writeLine(text: String) {
      output.add(text)
    }
  }
}
