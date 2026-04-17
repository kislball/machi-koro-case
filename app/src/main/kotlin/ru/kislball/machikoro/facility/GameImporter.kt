package ru.kislball.machikoro.facility

import ru.kislball.machikoro.game.Game

interface GameImporter {
    fun import(content: String): Game
}
