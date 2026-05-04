package ru.kislball.machikoro.storage

import ru.kislball.machikoro.facility.GameDriver

data class StoredGame(
    val driver: GameDriver,
    val catalogId: String,
)
