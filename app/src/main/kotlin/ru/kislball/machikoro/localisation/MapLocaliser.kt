package ru.kislball.machikoro.localisation

import ru.kislball.machikoro.exceptions.InvalidLocalisationInputException
import ru.kislball.machikoro.exceptions.LocalisationKeyNotFoundException
import ru.kislball.machikoro.exceptions.require

open class MapLocaliser(private val map: Map<String, (Any) -> String>) : Localiser {
  override fun localise(key: String, obj: Any): String {
    require(map.containsKey(key)) { LocalisationKeyNotFoundException(key) }
    val formatter = map.getValue(key)
    return try {
      formatter(obj)
    } catch (e: ClassCastException) {
      throw InvalidLocalisationInputException(key).apply { initCause(e) }
    }
  }
}
