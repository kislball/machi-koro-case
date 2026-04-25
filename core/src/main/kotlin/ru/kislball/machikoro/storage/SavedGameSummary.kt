package ru.kislball.machikoro.storage

import java.time.Instant

data class SavedGameSummary(
    val name: String,
    val playerNames: List<String>,
    val createdAt: Instant,
    val finished: Boolean,
    val winnerName: String?,
    val catalogId: String,
)
