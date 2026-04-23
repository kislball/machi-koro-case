package ru.kislball.machikoro.cli.command

import ru.kislball.machikoro.cli.error.CLIException
import ru.kislball.machikoro.cli.session.CLIMode
import ru.kislball.machikoro.cli.session.CLISession
import ru.kislball.machikoro.cli.session.ReactiveGame
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.facility.GameFactory
import ru.kislball.machikoro.game.step.PendingStepPhase

internal fun managementCommands(session: CLISession): List<Command> {
  return listOf(
      object : Command("exit", CLIMode.MANAGEMENT) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          session.shouldExit = true
        }
      },
      object : Command("list", CLIMode.MANAGEMENT) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          val games = context.storage.list()
          if (games.isEmpty()) {
            context.printLine("cli.games.empty")
          } else {
            games.forEach(context::printRaw)
          }
        }
      },
      object : Command("top", CLIMode.MANAGEMENT) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          val top = context.storage.top()
          if (top.isEmpty()) {
            context.printLine("cli.games.empty")
          } else {
            top.forEach { context.printLine("cli.top.entry", it) }
          }
        }
      },
      object : Command("delete", CLIMode.MANAGEMENT) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          require(arguments.size == 1) { "delete <name>" }
          context.storage.delete(arguments.single())
          context.printLine("cli.games.deleted", arguments.single())
        }
      },
      object : Command("load", CLIMode.MANAGEMENT) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          require(arguments.size == 1) { "load <name>" }
          val game = context.storage.load(arguments.single()).withObserver(context)
          session.activeGame = game
          session.mode = CLIMode.GAME
          context.printLine("cli.games.loaded", arguments.single())
        }
      },
      object : Command("start", CLIMode.MANAGEMENT) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          context.printLine("cli.start.players_prompt")
          val raw = context.io.readLine().orEmpty()
          val players = raw.split(",").map { it.trim() }.filter { it.isNotBlank() }
          if (players.isEmpty()) {
            throw CLIException("cli.start.players_invalid")
          }
          val game = ReactiveGame(GameFactory.createDriver(context.catalog, players)).withObserver(context)
          session.activeGame = game
          session.mode = CLIMode.GAME
        }
      },
  )
}

private fun ReactiveGame.withObserver(context: CommandContext): ReactiveGame {
  driver.observeEffects { effect: Effect, _: PendingStepPhase ->
    effectLog.add(context.localiser.localise("cli.effect", effect))
  }
  return this
}
