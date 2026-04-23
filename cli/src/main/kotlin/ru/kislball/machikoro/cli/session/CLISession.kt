package ru.kislball.machikoro.cli.session

import ru.kislball.machikoro.facility.GameDriver

data class CLISession(
    var mode: CLIMode = CLIMode.MANAGEMENT,
    var activeGame: ReactiveGame? = null,
    var shouldExit: Boolean = false,
)

data class ReactiveGame(
    val driver: GameDriver,
    val effectLog: MutableList<String> = mutableListOf(),
)
