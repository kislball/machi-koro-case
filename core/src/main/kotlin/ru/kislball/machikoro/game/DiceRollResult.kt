package ru.kislball.machikoro.game

data class DiceRollResult(val player: Player, val diceThrown: List<Int>)

data class IntermediateRollResult(val result: DiceRollResult)
