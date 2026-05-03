package ru.kislball.machikoro.facility

import ru.kislball.machikoro.facility.payload.GamePayload

interface GameImporter {
  fun import(s: String): GamePayload
}
