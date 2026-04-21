package ru.kislball.machikoro.cards

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.exceptions.GameException

class CardCatalogTest {
  @Test
  fun `get returns card by id`() {
    val card = StubCard("cards.bakery")
    val catalog = CardCatalog(card)

    assertEquals(card, catalog["cards.bakery"])
  }

  @Test
  fun `get returns null for missing id`() {
    val catalog = CardCatalog(StubCard("cards.cafe"))

    assertNull(catalog["missing.card"])
  }

  @Test
  fun `constructor rejects duplicate ids`() {
    assertFailsWith<GameException> { CardCatalog(StubCard("cards.same"), StubCard("cards.same")) }
  }
}
