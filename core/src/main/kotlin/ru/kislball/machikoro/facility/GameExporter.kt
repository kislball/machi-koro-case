package ru.kislball.machikoro.facility

import ru.kislball.machikoro.facility.payload.GamePayload

interface GameExporter {
  fun export(g: GamePayload): String
}
