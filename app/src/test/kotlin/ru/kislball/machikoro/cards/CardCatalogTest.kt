package ru.kislball.machikoro.cards

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import ru.kislball.machikoro.StubCard

class CardCatalogTest {
  @Test
  fun `allKinds returns all enum entries`() {
    assertEquals(CardKind.entries, CardCatalog.allKinds())
  }

  @Test
  fun `kindsByType filters by type`() {
    val sights = CardCatalog.kindsByType(CardType.SIGHT)

    assertTrue(sights.all { it.type == CardType.SIGHT })
    assertTrue(CardKind.TRAIN_STATION in sights)
  }

  @Test
  fun `contains returns true for known kind`() {
    assertTrue(CardCatalog.contains(CardKind.RANCH))
  }

  @Test
  fun `registerCreator and getCreator roundtrip`() {
    CardCatalog.registerCreator(CardKind.RANCH) { StubCard(CardKind.RANCH) }

    val creator = CardCatalog.getCreator(CardKind.RANCH)

    assertNotNull(creator)
    assertEquals(CardKind.RANCH, creator.invoke().kind)
  }

  @Test
  fun `registerCreators registers all creators`() {
    CardCatalog.registerCreators(
        mapOf(
            CardKind.BAKERY to { StubCard(CardKind.BAKERY) },
            CardKind.CAFE to { StubCard(CardKind.CAFE) },
        ))

    assertNotNull(CardCatalog.getCreator(CardKind.BAKERY))
    assertNotNull(CardCatalog.getCreator(CardKind.CAFE))
  }
}
