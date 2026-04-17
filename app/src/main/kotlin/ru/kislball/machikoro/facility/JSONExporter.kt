package ru.kislball.machikoro.facility

import ru.kislball.machikoro.game.Game

class JSONExporter : GameExporter {
    override fun export(game: Game): String {
        val serializedPlayers =
            game.players.joinToString(separator = ",") { player ->
                val serializedCards = player.cards.joinToString(separator = ",") { "\"${it.kind.name}\"" }
                """{"name":"${player.name.jsonEscape()}","balance":${player.balance},"cards":[${serializedCards}]}"""
            }
        return """{"players":[${serializedPlayers}]}"""
    }
}

private fun String.jsonEscape(): String {
    return buildString {
        for (ch in this@jsonEscape) {
            when (ch) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\b' -> append("\\b")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> {
                    if (ch.code < 32) {
                        append("\\u%04x".format(ch.code))
                    } else {
                        append(ch)
                    }
                }
            }
        }
    }
}
