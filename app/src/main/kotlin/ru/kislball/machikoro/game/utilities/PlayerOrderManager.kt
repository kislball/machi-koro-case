package ru.kislball.machikoro.game.utilities

import ru.kislball.machikoro.game.Player

class PlayerOrderManager(val players: List<Player>) {
    var queue = mutableListOf<Player>()
    private set

    private fun ensureQueueNotEmpty() {
        if (queue.isEmpty()) {
            queue = players.toMutableList()
        }
    }

    fun peekNext(): Player {
        ensureQueueNotEmpty()
        return queue.first()
    }

    fun next(): Player {
        ensureQueueNotEmpty()
        val player = queue.first()
        queue.removeAt(0)
        return player
    }

    fun skipTurn(p: Player) {
        queue.remove(p)
    }

    fun setNext(p: Player) {
        queue.add(0, p)
    }
}
