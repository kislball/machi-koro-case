package ru.kislball.machikoro.cards

import ru.kislball.machikoro.game.Step
import ru.kislball.machikoro.game.Triggerable

abstract class Card : Triggerable() {
    abstract val cardNameKey: String
    abstract val cardNameDescription: String

    abstract fun getPrice(s: Step): Int
}