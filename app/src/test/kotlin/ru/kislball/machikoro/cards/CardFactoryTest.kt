package ru.kislball.machikoro.cards

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.kislball.machikoro.StubCard

class CardFactoryTest {
  @Test
  fun `canCreate is false when creator is missing`() {
    assertFalse(CardFactory.canCreate(CardKind.RADIO_TOWER))
  }

  @Test
  fun `canCreate is true when creator exists`() {
    CardCatalog.registerCreator(CardKind.WHEAT_FIELD) { StubCard(CardKind.WHEAT_FIELD) }

    assertTrue(CardFactory.canCreate(CardKind.WHEAT_FIELD))
  }

  @Test
  fun `create returns card from registered creator`() {
    CardCatalog.registerCreator(CardKind.FOREST) { StubCard(CardKind.FOREST) }

    val card = CardFactory.create(CardKind.FOREST)

    assertEquals(CardKind.FOREST, card.kind)
  }

  @Test
  fun `create throws for missing creator`() {
    val error = assertFailsWith<IllegalArgumentException> { CardFactory.create(CardKind.STADIUM) }

    assertTrue(error.message!!.contains("No card creator is registered"))
  }
}
