package ru.kislball.machikoro.game.markers

import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.utilities.getOrNull

private class CanRethrowDice(val can: Boolean = true)

fun Player.canRethrowDice(): Boolean {
    return resources.getOrNull<CanRethrowDice>()?.can ?: false
}

fun Player.setCanRethrowDice(value: Boolean) {
    resources.set(CanRethrowDice(value))
}
