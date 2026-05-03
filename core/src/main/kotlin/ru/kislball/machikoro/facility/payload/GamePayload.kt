package ru.kislball.machikoro.facility.payload

data class GamePayload(
    val players: List<PlayerPayload>,
    val metadata: GamePayloadMetadata? = null,
)

data class GamePayloadMetadata(
    val winner: String? = null,
    val catalogId: String? = null,
    val finished: Boolean? = null,
)

data class PlayerPayload(val name: String, val balance: Int, val cards: List<String>)

