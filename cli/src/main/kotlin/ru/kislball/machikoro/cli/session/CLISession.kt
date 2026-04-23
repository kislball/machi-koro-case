package ru.kislball.machikoro.cli.session

import ru.kislball.machikoro.facility.GameDriver

data class CLISession(
    var mode: CLIMode = CLIMode.MANAGEMENT,
    var activeGame: ActiveCliGame? = null,
    var shouldExit: Boolean = false,
)

data class ActiveCliGame(
    val driver: GameDriver,
    val catalogId: String,
    val effectLog: MutableList<String> = mutableListOf(),
)
