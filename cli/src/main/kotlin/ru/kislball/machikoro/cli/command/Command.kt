package ru.kislball.machikoro.cli.command

import ru.kislball.machikoro.cli.session.CLIMode

abstract class Command(
    val name: String,
    val mode: CLIMode,
) {
  open fun matches(commandName: String): Boolean = commandName == name

  abstract fun execute(arguments: List<String>, context: CommandContext)
}
