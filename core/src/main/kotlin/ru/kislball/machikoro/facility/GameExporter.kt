package ru.kislball.machikoro.facility

import ru.kislball.machikoro.game.Game

interface GameExporter {
  fun export(game: Game): String
}
