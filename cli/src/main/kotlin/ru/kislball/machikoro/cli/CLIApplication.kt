package ru.kislball.machikoro.cli

import ru.kislball.machikoro.effects.cards.swap.SwapCardsInputEffect
import ru.kislball.machikoro.effects.dice.RethrowDiceInputEffect
import ru.kislball.machikoro.effects.money.PickAndChargeUserInputEffect
import ru.kislball.machikoro.effects.order.GivePlayerAdditionalStepInputEffect
import ru.kislball.machikoro.exceptions.GameException
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.PendingStepPhase
import ru.kislball.machikoro.game.utilities.getOrNull
import ru.kislball.machikoro.localisation.localiseOrKey

class CLIApplication(
    private val io: CLIIO = StdCLIIO,
    private val storage: CLIStorage = CLIStorage.default(),
) {
  private val session = CLISession()
  private val context = CommandContext(io, storage, session, GameAutoAdvance())
  private val commands = managementCommands(session) + gameCommands(session)

  fun run() {
    while (!session.shouldExit) {
      printPrompt()
      val line = io.readLine() ?: break
      if (line.isBlank()) continue
      execute(line)
    }
  }

  fun execute(line: String) {
    val parts = line.trim().split(Regex("\\s+"))
    val commandName = parts.first()
    val arguments = parts.drop(1)
    val command =
        commands.firstOrNull { it.mode == session.mode && it.matches(commandName) }
            ?: return context.printLine("cli.unknown_command", commandName)
    try {
      command.execute(arguments, context)
      flushGameState()
    } catch (exception: CLIException) {
      context.printRaw(context.localiser.localise(exception.key, exception.payload))
    } catch (exception: GameException) {
      context.printRaw(exception.localiseOrKey(context.localiser))
    } catch (exception: IllegalArgumentException) {
      context.printLine("cli.invalid_arguments", exception.message ?: "")
    }
  }

  private fun flushGameState() {
    val game = session.activeGame ?: return
    context.autoAdvance.advance(game)
    game.effectLog.forEach(context::printRaw)
    game.effectLog.clear()

    val current = game.driver.game.currentStepPhase as? PendingStepPhase ?: return
    current.results.getOrNull<DiceRollResult>()?.let { context.printLine("cli.dice.current", it) }
    val input = game.driver.game.inputEffects.peek()
    when (input) {
      is RethrowDiceInputEffect -> context.printLine("cli.awaiting.rethrow", input.player)
      is PickAndChargeUserInputEffect -> context.printLine("cli.awaiting.pick_player", input.player)
      is SwapCardsInputEffect -> context.printLine("cli.awaiting.swap", input.player)
      is GivePlayerAdditionalStepInputEffect -> context.printLine("cli.awaiting.additional_step", input.player)
    }
  }

  private fun printPrompt() {
    val prompt =
        when (session.mode) {
          CLIMode.MANAGEMENT -> context.localiser.localise("cli.management.prompt")
          CLIMode.GAME ->
              context.localiser.localise(
                  "cli.game.prompt",
                  session.activeGame?.driver?.game?.currentPlayer ?: Player("?"),
              )
        }
    io.writeLine(prompt)
  }
}
