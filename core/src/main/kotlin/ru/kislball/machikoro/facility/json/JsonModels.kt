package ru.kislball.machikoro.facility.json

data class GameJson(
    val players: List<PlayerJson>,
    val metadata: GameMetadataJson? = null,
)

data class GameMetadataJson(
    val winner: String? = null,
)

data class PlayerJson(val name: String, val balance: Int, val cards: List<String>)
