package ru.kislball.machikoro.localisation

import kotlin.test.Test
import kotlin.test.assertEquals

class CompoundLocaliserTest {
  @Test
  fun `compound localiser falls back when primary key is missing`() {
    val primary = MapLocaliser(mapOf("primary" to { _: Any -> "first" }))
    val fallback = MapLocaliser(mapOf("fallback" to { _: Any -> "second" }))

    val localiser = CompoundLocaliser(primary, fallback)

    assertEquals("second", localiser.localise("fallback"))
  }

  @Test
  fun `compound localiser prefers primary value`() {
    val primary = MapLocaliser(mapOf("shared" to { _: Any -> "first" }))
    val fallback = MapLocaliser(mapOf("shared" to { _: Any -> "second" }))

    val localiser = CompoundLocaliser(primary, fallback)

    assertEquals("first", localiser.localise("shared"))
  }
}
