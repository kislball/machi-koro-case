package ru.kislball.machikoro.actions

import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step
import ru.kislball.machikoro.game.DiceRolledStep

abstract class PlayerAction(val player: Player) {
    abstract fun checkValid(s: Step)
    abstract fun getEffect(s: Step): Effect
}