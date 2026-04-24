@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

package ru.kislball.machikoro.cli.integration

import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.cards.common.OverrideStarterCardsCatalog
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.cli.CLIApplication
import ru.kislball.machikoro.cli.catalog.CLICatalogDefinition
import ru.kislball.machikoro.cli.catalog.CLICatalogRegistry
import ru.kislball.machikoro.cli.io.CLIIO
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInputEffect
import ru.kislball.machikoro.effects.money.PickAndChargeUserInputEffect
import ru.kislball.machikoro.effects.order.GivePlayerAdditionalStepInputEffect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.storage.GameStorage

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
    val app = CLIApplication(io, storageFor(catalogs), catalogs)

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
    assertTrue(
        io.output.any {
          it.startsWith("Игрок alice: баланс ") && it.contains("Пшеница") && it.contains("Пекарня")
        })
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
                "roll 2",
                "buyCard cards.supermarket",
                "info alice",
                "save upgraded",
            ))
    val catalogs = registryFor(catalog)
    val storage = storageFor(catalogs)
    val app = CLIApplication(io, storage, catalogs)

    app.run()

    assertContainsInOrder(
        io.output,
        listOf(
            "management> ",
            "Введите имена игроков через запятую",
            "Ожидается выбор количества кубиков для alice",
            "game(alice)> ",
            "Ожидается покупка карты для alice",
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
    val app = CLIApplication(io, storageFor(catalogs), catalogs)

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

  @Test
  fun `run allows skipping card purchase`() {
    val io = ScriptedCLIIO(mutableListOf("start", "alice,bob", "skipBuy", "info bob"))
    val catalogs = CLICatalogRegistry.default()
    val app = CLIApplication(io, CLIStorage(tempDir, catalogs), catalogs)

    app.run()

    assertContainsInOrder(
        io.output,
        listOf(
            "management> ",
            "Введите имена игроков через запятую",
            "Ожидается покупка карты для alice",
            "game(alice)> ",
            "Ввод применён",
            "game(bob)> ",
            "Игрок bob: баланс ",
        ),
    )
  }

  @Test
  fun `run resolves pickPlayer for special card input`() {
    val catalog = testCatalog(TestPickPlayerCard())
    val io =
        ScriptedCLIIO(
            mutableListOf(
                "start",
                "alice,bob",
                "pickPlayer bob",
                "info alice",
                "info bob",
            ))
    val catalogs = registryFor(catalog)
    val app = CLIApplication(io, storageFor(catalogs), catalogs)

    app.run()

    assertContainsInOrder(
        io.output,
        listOf(
            "management> ",
            "Введите имена игроков через запятую",
            "Ожидается ввод",
            "game(alice)> ",
            "Ввод применён",
        ),
    )
    assertTrue(io.output.any { it.contains("Ожидается выбор игрока для alice") })
    assertTrue(
        io.output.any { it.contains("Игрок alice: баланс ") && it.contains("cards.cli_test_pick") })
    assertTrue(io.output.any { it.contains("Игрок bob: баланс 1") })
  }

  @Test
  fun `run resolves swap for special card input`() {
    val catalog = testCatalog(TestSwapCard())
    val io =
        ScriptedCLIIO(
            mutableListOf(
                "start",
                "alice,bob",
                "swap bob cards.cli_test_beta cards.cli_test_alpha",
                "save swapped",
            ))
    val catalogs = registryFor(catalog)
    val storage = storageFor(catalogs)
    val app = CLIApplication(io, storage, catalogs)

    app.run()

    assertContainsInOrder(
        io.output,
        listOf(
            "management> ",
            "Введите имена игроков через запятую",
            "Ожидается ввод",
            "game(alice)> ",
            "Ввод применён",
            "Игра сохранена: swapped",
        ),
    )
    assertTrue(io.output.any { it.contains("Ожидается обмен карт для alice") })

    val savedGame = storage.load("swapped").driver.game
    val alice = savedGame.players.first { it.name == "alice" }
    val bob = savedGame.players.first { it.name == "bob" }
    assertEquals(
        listOf("cards.cli_test_beta", "cards.cli_test_beta", "cards.cli_test_swap").sorted(),
        alice.cards.map { it.cardId }.sorted(),
    )
    assertEquals(
        listOf("cards.cli_test_alpha", "cards.cli_test_alpha", "cards.cli_test_swap").sorted(),
        bob.cards.map { it.cardId }.sorted(),
    )
  }

  @Test
  fun `run shows awaiting roll choice when player can throw two dice`() {
    val catalog =
        OverrideStarterCardsCatalog(
            StandardCatalog.getCardList(),
            listOf("cards.wheat", "cards.bakery", "cards.railway_station"),
        )
    val io = ScriptedCLIIO(mutableListOf("start", "alice,bob", "roll 2", "exit"))
    val catalogs = registryFor(catalog)
    val app = CLIApplication(io, CLIStorage(tempDir, catalogs), catalogs)

    app.run()

    assertContainsInOrder(
        io.output,
        listOf(
            "management> ",
            "Введите имена игроков через запятую",
            "Ожидается выбор количества кубиков для alice",
            "game(alice)> ",
            "Кубики брошены",
        ),
    )
  }

  @Test
  fun `run resolves additional step decision for special card input`() {
    val catalog = testCatalog(TestAdditionalStepCard())
    val io = ScriptedCLIIO(mutableListOf("start", "alice,bob", "takeAdditionalStep", "exit"))
    val catalogs = registryFor(catalog)
    val app = CLIApplication(io, CLIStorage(tempDir, catalogs), catalogs)

    app.run()

    assertContainsInOrder(
        io.output,
        listOf(
            "management> ",
            "Введите имена игроков через запятую",
            "Ожидается ввод",
            "game(alice)> ",
            "Ввод применён",
        ),
    )
    assertTrue(io.output.any { it.contains("Ожидается решение о дополнительном ходе для alice") })
  }

  private fun assertContainsInOrder(output: List<String>, expectedParts: List<String>) {
    var currentIndex = 0
    for (expectedPart in expectedParts) {
      val foundIndex =
          output
              .withIndex()
              .firstOrNull { (index, line) -> index >= currentIndex && line.contains(expectedPart) }
              ?.index ?: -1
      assertTrue(
          foundIndex >= 0, "Missing output containing: $expectedPart\nActual output: $output")
      currentIndex = foundIndex + 1
    }
  }

  private fun registryFor(catalog: CardCatalog): CLICatalogRegistry {
    return CLICatalogRegistry(
        definitions =
            listOf(
                CLICatalogDefinition("standard", StandardCatalog),
                CLICatalogDefinition("custom", catalog),
            ),
        defaultCatalogId = "custom",
    )
  }

  private fun storageFor(catalogs: CLICatalogRegistry): GameStorage {
    return GameStorage(tempDir, catalogs.defaultCatalogId) { catalogId ->
      catalogs.get(catalogId)?.catalog
    }
  }

  private fun testCatalog(card: Card): CardCatalog {
    val first = TestBasicCard("cards.cli_test_alpha")
    val second = TestBasicCard("cards.cli_test_beta")
    return object : CardCatalog(listOf(first, second, card)) {
      override fun getStarterCards(): List<Card> {
        return listOf(
            checkNotNull(this[first.cardId]),
            checkNotNull(this[second.cardId]),
            checkNotNull(this[card.cardId]),
        )
      }
    }
  }

  private class TestBasicCard(cardId: String) :
      Card(cardId, CardType.ENTERPRISE, icon = CardIcon.SHOP) {
    override fun getPrice(s: StepPhase): Int = 1

    override fun getEffect(s: StepPhase, possessor: Player?): Effect {
      error("TestBasicCard should never trigger")
    }

    override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean = false
  }

  private class TestPickPlayerCard :
      Card("cards.cli_test_pick", CardType.ENTERPRISE, icon = CardIcon.SPECIAL) {
    override fun getPrice(s: StepPhase): Int = 1

    override fun getEffect(s: StepPhase, possessor: Player?): Effect {
      return PickAndChargeUserInputEffect.getAwaiter(checkNotNull(possessor), 2)
    }

    override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
      return possessor == stepPhase.currentPlayer
    }
  }

  private class TestSwapCard :
      Card("cards.cli_test_swap", CardType.ENTERPRISE, icon = CardIcon.SPECIAL) {
    override fun getPrice(s: StepPhase): Int = 1

    override fun getEffect(s: StepPhase, possessor: Player?): Effect {
      return SwapCardsInputEffect.getAwaiter(checkNotNull(possessor))
    }

    override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
      return possessor == stepPhase.currentPlayer
    }
  }

  private class TestAdditionalStepCard :
      Card("cards.cli_test_additional_step", CardType.ENTERPRISE, icon = CardIcon.SPECIAL) {
    override fun getPrice(s: StepPhase): Int = 1

    override fun getEffect(s: StepPhase, possessor: Player?): Effect {
      return GivePlayerAdditionalStepInputEffect.getAwaiter(checkNotNull(possessor))
    }

    override fun isTriggered(stepPhase: StepPhase, possessor: Player?): Boolean {
      return possessor == stepPhase.currentPlayer
    }
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
