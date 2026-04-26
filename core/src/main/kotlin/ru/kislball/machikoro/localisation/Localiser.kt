package ru.kislball.machikoro.localisation

import ru.kislball.machikoro.exceptions.GameException

interface Localiser {
  fun localise(key: String, obj: Any = Unit): String
}

fun GameException.localise(localiser: Localiser): String {
  return localiser.localise(key, Unit)
}

fun GameException.localiseOrKey(localiser: Localiser?): String {
  return localiser?.localise(key, Unit) ?: key
}
