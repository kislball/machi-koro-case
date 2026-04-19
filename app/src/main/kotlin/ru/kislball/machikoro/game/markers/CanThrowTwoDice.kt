package ru.kislball.machikoro.game.markers

import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.getOrNull

private class CanThrowTwoDice(val can: Boolean = true)

fun Player.canThrowTwoDice(): Boolean {
  return resources.getOrNull<CanThrowTwoDice>()?.can ?: false
}

fun Player.setThrowTwoDice(value: Boolean) {
  resources.set(CanThrowTwoDice(value))
}
