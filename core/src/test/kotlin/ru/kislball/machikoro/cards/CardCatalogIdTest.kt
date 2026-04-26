package ru.kislball.machikoro.cards

import kotlin.test.Test
import kotlin.test.assertEquals
import ru.kislball.machikoro.cards.common.catalogId
import ru.kislball.machikoro.cards.standard.StandardCatalog

class CardCatalogIdTest {
  @Test
  fun `standard catalog exposes standard id`() {
    assertEquals("standard", StandardCatalog.catalogId)
  }
}
