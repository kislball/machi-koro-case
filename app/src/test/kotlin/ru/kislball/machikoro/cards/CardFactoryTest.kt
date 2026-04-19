package ru.kislball.machikoro.cards

import kotlin.test.Test
import kotlin.test.assertNotNull
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.standard.StandardCatalog

class CardFactoryTest {
  @Test
  fun `standard catalog contains known cards`() {
    assertNotNull(StandardCatalog["cards.cafe"])
    assertNotNull(StandardCatalog["cards.family"])
    assertNotNull(StandardCatalog["cards.railway_station"])
    assertNotNull(StandardCatalog["cards.tv_tower"])
  }

  @Test
  fun `catalog can be built from custom cards`() {
    val catalog = CardCatalog(StandardCatalog["cards.cafe"]!!)

    assertNotNull(catalog["cards.cafe"])
  }
}
