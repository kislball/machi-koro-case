package ru.kislball.machikoro.facility

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.common.CardCatalogDefinition
import ru.kislball.machikoro.cards.common.CardCatalogResolver
import ru.kislball.machikoro.facility.json.JSONExporter
import ru.kislball.machikoro.facility.json.JSONImporter
import ru.kislball.machikoro.facility.payload.GamePayload
import ru.kislball.machikoro.facility.payload.PlayerPayload
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.triggers.special.SightsCollectedTrigger

class JsonImportExportTest {
  @Test
  fun `export serializes players balances and cards`() {
    val player = Player("A\"\\\n")
    player.balance = 7
    player.cards.add(StubCard("cards.wheat_field"))
    val game = Game(listOf(player))

    val json = JSONExporter().export(GamePayload(game))

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

    val payload = JSONImporter().import(content)
    payload.catalogResolver = resolverFor(catalog)
    val game = GameFactory.createDriver(payload).game

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

    val payload = JSONImporter().import(content)
    payload.catalogResolver = resolverFor(catalog)
    val game = GameFactory.createDriver(payload).game

    assertEquals("a\\\"b", game.players[0].name)
    assertEquals("x\\u0020y", game.players[1].name)
  }

  @Test
  fun `export and import preserve winner metadata`() {
    val catalog = CardCatalog()
    val alice = Player("alice")
    val bob = Player("bob")
    val game = Game(catalog, listOf(alice, bob), SightsCollectedTrigger(), alice)

    val payload = GamePayload(game)
    val json = JSONExporter().export(payload)
    val importedPayload = JSONImporter().import(json)
    importedPayload.catalogResolver = resolverFor(catalog)
    val imported = GameFactory.createDriver(importedPayload).game

    assertTrue(json.contains("\"winner\":\"alice\""))
    assertEquals("alice", imported.winner?.name)
    assertTrue(imported.finished)
  }

  @Test
  fun `export includes catalog metadata when provided`() {
    val game = Game(listOf(Player("alice")))

    val json = JSONExporter("standard").export(GamePayload(game))

    assertTrue(json.contains("\"catalogId\":\"standard\""))
  }

  @Test
  fun `import rejects malformed root json`() {
    assertFailsWith<Exception> { JSONImporter().import("{}") }
  }

  @Test
  fun `game factory creates driver from game payload model`() {
    val catalog = CardCatalog(StubCard("cards.wheat_field"))
    val payload =
        GamePayload(
            players =
                listOf(PlayerPayload("alice", balance = 3, cards = listOf("cards.wheat_field"))))
    payload.catalogResolver = resolverFor(catalog)

    val game = GameFactory.createDriver(payload).game

    assertEquals("alice", game.players.single().name)
    assertEquals("cards.wheat_field", game.players.single().cards.single().cardId)
  }

  @Test
  fun `game payload catalog getter resolves catalog dynamically`() {
    val game = Game(listOf(Player("alice")))

    val payload = GamePayload(game)

    assertEquals(game.catalog, payload.catalog)
  }

  private fun resolverFor(catalog: CardCatalog): CardCatalogResolver {
    return CardCatalogResolver(CardCatalogDefinition("standard", catalog))
  }
}
