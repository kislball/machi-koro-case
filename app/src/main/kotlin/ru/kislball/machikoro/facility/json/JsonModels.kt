package ru.kislball.machikoro.facility.json

data class GameJson(val players: List<PlayerJson>)

data class PlayerJson(val name: String, val balance: Int, val cards: List<String>)
