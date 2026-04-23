@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

package ru.kislball.machikoro.cli.integration

import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.kislball.machikoro.cards.common.OverrideStarterCardsCatalog
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.cli.catalog.CLICatalogDefinition
import ru.kislball.machikoro.cli.catalog.CLICatalogRegistry
import ru.kislball.machikoro.cli.CLIApplication
import ru.kislball.machikoro.cli.io.CLIIO
import ru.kislball.machikoro.cli.storage.CLIStorage

class CLIFlowIntegrationTest {
  private val tempDir = createTempDirectory("machikoro-cli-integration")

  @AfterTest
  fun tearDown() {
    tempDir.deleteRecursively()
  }

  @Test
  fun `run processes a full cli session through abstract io`() {
    val io =
        ScriptedCLIIO(
            mutableListOf(
                "start",
                "alice,bob",
                "info",
                "save demo",
                "exit",
                "list",
                "load demo",
                "info alice",
            ))
    val catalogs = CLICatalogRegistry.default()
    val app = CLIApplication(io, CLIStorage(tempDir, catalogs), catalogs)

    app.run()

    assertContainsInOrder(
        io.output,
        listOf(
            "management> ",
            "Введите имена игроков через запятую",
            "Кубики брошены",
            "game(alice)> ",
            "Игрок alice: баланс ",
            "game(alice)> ",
            "Игра сохранена: demo",
            "Выход в режим управления",
            "management> ",
            "demo",
            "management> ",
            "Игра загружена: demo",
            "game(alice)> ",
        ),
    )
    assertTrue(io.output.any { it.startsWith("Игрок alice: баланс ") && it.contains("Пшеница") && it.contains("Пекарня") })
    assertTrue(io.output.any { it.startsWith("Кости: ") })
    assertTrue(tempDir.resolve("demo.json").exists())
  }

  @Test
  fun `run persists purchased cards and advances to the next player`() {
    val catalog =
        OverrideStarterCardsCatalog(
            StandardCatalog.getCardList(),
            listOf(
                "cards.wheat",
                "cards.bakery",
                "cards.shopping_centre",
                "cards.railway_station",
            ),
        )
    val io =
        ScriptedCLIIO(
            mutableListOf(
                "start",
                "alice,bob",
                "buyCard cards.supermarket",
                "info alice",
                "save upgraded",
            ))
    val catalogs = registryFor(catalog)
    val storage = CLIStorage(tempDir, catalogs)
    val app = CLIApplication(io, storage, catalogs)

    app.run()

    assertContainsInOrder(
        io.output,
        listOf(
            "management> ",
            "Введите имена игроков через запятую",
            "game(alice)> ",
            "game(bob)> ",
            "Игрок alice: баланс ",
            "Игра сохранена: upgraded",
        ),
    )
    assertTrue(
        io.output.any { line ->
          line.startsWith("Игрок alice: баланс ") &&
              line.contains("Супермаркет") &&
              line.contains("Вокзал") &&
              line.contains("Торговый центр")
        })

    val savedGame = storage.load("upgraded").driver.game
    val alice = savedGame.players.first { it.name == "alice" }
    assertEquals(
        listOf(
            "cards.wheat",
            "cards.bakery",
            "cards.shopping_centre",
            "cards.railway_station",
            "cards.supermarket",
        ),
        alice.cards.map { it.cardId },
    )
  }

  @Test
  fun `run shows awaiting rethrow when starter cards grant tv tower`() {
    val catalog =
        OverrideStarterCardsCatalog(
            StandardCatalog.getCardList(),
            listOf("cards.wheat", "cards.bakery", "cards.tv_tower"),
        )
    val io = ScriptedCLIIO(mutableListOf("start", "alice,bob", "rethrow", "exit"))
    val catalogs = registryFor(catalog)
    val app = CLIApplication(io, CLIStorage(tempDir, catalogs), catalogs)

    app.run()

    assertContainsInOrder(
        io.output,
        listOf(
            "management> ",
            "Введите имена игроков через запятую",
            "Кубики брошены",
            "Ожидается решение о перебросе для alice",
            "game(alice)> ",
            "Ввод применён",
            "game(alice)> ",
            "Выход в режим управления",
        ),
    )
  }

  private fun assertContainsInOrder(output: List<String>, expectedParts: List<String>) {
    var currentIndex = 0
    for (expectedPart in expectedParts) {
      val foundIndex =
          output.withIndex().firstOrNull { (index, line) ->
            index >= currentIndex && line.contains(expectedPart)
          }?.index ?: -1
      assertTrue(foundIndex >= 0, "Missing output containing: $expectedPart\nActual output: $output")
      currentIndex = foundIndex + 1
    }
  }

  private fun registryFor(catalog: OverrideStarterCardsCatalog): CLICatalogRegistry {
    return CLICatalogRegistry(
        definitions =
            listOf(
                CLICatalogDefinition("standard", StandardCatalog),
                CLICatalogDefinition("custom", catalog),
            ),
        defaultCatalogId = "custom",
    )
  }

  private class ScriptedCLIIO(
      private val input: MutableList<String>,
  ) : CLIIO {
    val output = mutableListOf<String>()

    override fun readLine(): String? = input.removeFirstOrNull()

    override fun writeLine(text: String) {
      output.add(text)
    }
  }
}
