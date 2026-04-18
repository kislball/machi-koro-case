package ru.kislball.machikoro.game.markers

import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.contains

class CanThrowTwoDice

fun Player.canThrowTwoDice(): Boolean {
    return resources.contains<CanThrowTwoDice>()
}
