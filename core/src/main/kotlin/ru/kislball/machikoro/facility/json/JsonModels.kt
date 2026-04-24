package ru.kislball.machikoro.facility.json

data class GameJson(
    val players: List<PlayerJson>,
    val metadata: GameMetadataJson? = null,
)

data class GameMetadataJson(
    val winner: String? = null,
    val catalogId: String? = null,
    val finished: Boolean? = null,
)

data class PlayerJson(val name: String, val balance: Int, val cards: List<String>)
