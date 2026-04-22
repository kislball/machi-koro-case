package ru.kislball.machikoro.localisation

import ru.kislball.machikoro.exceptions.LocalisationKeyNotFoundException

class CompoundLocaliser(
    private val primary: Localiser,
    private val fallback: Localiser,
) : Localiser {
  override fun localise(key: String, obj: Any): String {
    return try {
      primary.localise(key, obj)
    } catch (_: LocalisationKeyNotFoundException) {
      fallback.localise(key, obj)
    }
  }
}
