package ru.kislball.machikoro.facility

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player

class JsonImportExportTest {
  @Test
  fun `export serializes players balances and cards`() {
    val player = Player("A\"\\\n")
    player.balance = 7
    player.cards.add(StubCard("cards.wheat_field"))
    val game = Game(listOf(player))

    val json = JSONExporter().export(game)

    assertTrue(json.contains("\\\""))
    assertTrue(json.contains("\\\\"))
    assertTrue(json.contains("\\n"))
    assertTrue(json.contains("\"balance\":7"))
    assertTrue(json.contains("\"cards.wheat_field\""))
  }

  @Test
  fun `import parses game and reconstructs cards`() {
    val catalog = CardCatalog(StubCard("cards.wheat_field"))
    val content = """{"players":[{"name":"alice","balance":3,"cards":["cards.wheat_field"]}]}"""

    val game = JSONImporter(catalog).import(content)

    assertEquals(1, game.players.size)
    assertEquals("alice", game.players.single().name)
    assertEquals(3, game.players.single().balance)
    assertEquals("cards.wheat_field", game.players.single().cards.single().cardId)
  }

  @Test
  fun `import supports escaped characters and multiple players`() {
    val catalog = CardCatalog(StubCard("cards.bakery"))
    val content =
        """{"players":[{"name":"a\\\"b","balance":1,"cards":["cards.bakery"]},""" +
            """{"name":"x\\u0020y","balance":2,"cards":[]}]}"""

    val game = JSONImporter(catalog).import(content)

    assertEquals("a\\\"b", game.players[0].name)
    assertEquals("x\\u0020y", game.players[1].name)
  }

  @Test
  fun `import rejects malformed root json`() {
    assertFailsWith<IllegalArgumentException> { JSONImporter(CardCatalog()).import("{}") }
  }
}
