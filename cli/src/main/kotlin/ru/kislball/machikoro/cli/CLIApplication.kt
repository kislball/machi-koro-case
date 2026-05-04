package ru.kislball.machikoro.cli

import ru.kislball.machikoro.cli.catalog.CLICatalogRegistry
import ru.kislball.machikoro.cli.command.CommandContext
import ru.kislball.machikoro.cli.command.gameCommands
import ru.kislball.machikoro.cli.command.managementCommands
import ru.kislball.machikoro.cli.error.CLIException
import ru.kislball.machikoro.cli.game.GameAutoAdvance
import ru.kislball.machikoro.cli.io.CLIIO
import ru.kislball.machikoro.cli.io.StdCLIIO
import ru.kislball.machikoro.cli.session.CLIMode
import ru.kislball.machikoro.cli.session.CLISession
import ru.kislball.machikoro.effects.cards.buy.BuyCardInputEffect
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
import ru.kislball.machikoro.storage.GameStorage
import ru.kislball.machikoro.storage.GameStorageFactory

class CLIApplication(
    private val io: CLIIO = StdCLIIO,
    private val storage: GameStorage = defaultStorage(),
    catalogs: CLICatalogRegistry = CLICatalogRegistry.default(),
) {
  private val session = CLISession()
  private val context = CommandContext(io, storage, session, GameAutoAdvance(), catalogs)
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
    appendResultLogs(game)
    game.effectLog.forEach(context::printRaw)
    game.effectLog.clear()

    val current = game.driver.game.currentStepPhase as? PendingStepPhase ?: return
    if (game.driver.needsRollDecision(current.currentPlayer)) {
      context.printLine("cli.awaiting.roll", current.currentPlayer)
      return
    }
    val input = game.driver.game.inputEffects.peek()
    when (input) {
      is RethrowDiceInputEffect -> context.printLine("cli.awaiting.rethrow", input.player)
      is PickAndChargeUserInputEffect -> context.printLine("cli.awaiting.pick_player", input.player)
      is SwapCardsInputEffect -> context.printLine("cli.awaiting.swap", input.player)
      is BuyCardInputEffect -> context.printLine("cli.awaiting.buy_card", input.player)
      is GivePlayerAdditionalStepInputEffect ->
          context.printLine("cli.awaiting.additional_step", input.player)
    }
  }

  private fun appendResultLogs(game: ru.kislball.machikoro.cli.session.ActiveCliGame) {
    val current = game.driver.game.currentStepPhase as? PendingStepPhase ?: return
    val result = current.results.getOrNull<DiceRollResult>() ?: return
    val resultKey = current.stepNumber to result.diceThrown
    if (game.loggedDiceResultKey == resultKey) return

    game.loggedDiceResultKey = resultKey
    game.effectLog.add(context.localiser.localise("cli.result.dice.current", result))
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

  companion object {
    private fun defaultStorage(): GameStorage {
      val root = "${System.getProperty("user.dir")}/.machikoro-cli"
      val catalogs = CLICatalogRegistry.default()
      return GameStorageFactory.json(root, catalogs.defaultCatalogId, catalogs.resolver)
    }
  }
}
