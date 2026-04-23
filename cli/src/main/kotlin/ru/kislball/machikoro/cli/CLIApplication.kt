package ru.kislball.machikoro.cli

import ru.kislball.machikoro.effects.cards.swap.SwapCardsInput
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInputEffect
import ru.kislball.machikoro.effects.dice.RethrowDiceInputEffect
import ru.kislball.machikoro.effects.money.PickAndChargeUserInputEffect
import ru.kislball.machikoro.effects.order.GivePlayerAdditionalStepInputEffect
import ru.kislball.machikoro.exceptions.GameException
import ru.kislball.machikoro.facility.GameFactory
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.IntermediateRollResult
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
  private val commands = buildCommands()

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

  private fun buildCommands(): List<Command> {
    return listOf(
        object : Command("exit", CLIMode.MANAGEMENT) {
          override fun execute(arguments: List<String>, context: CommandContext) {
            session.shouldExit = true
          }
        },
        object : Command("exit", CLIMode.GAME) {
          override fun execute(arguments: List<String>, context: CommandContext) {
            session.activeGame = null
            session.mode = CLIMode.MANAGEMENT
            context.printLine("cli.game.exited")
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
        object : Command("save", CLIMode.GAME) {
          override fun execute(arguments: List<String>, context: CommandContext) {
            require(arguments.size == 1) { "save <name>" }
            context.storage.save(arguments.single(), requireGame())
            context.printLine("cli.games.saved", arguments.single())
          }
        },
        object : Command("info", CLIMode.GAME) {
          override fun execute(arguments: List<String>, context: CommandContext) {
            val game = requireGame().driver.game
            val player =
                arguments.singleOrNull()?.let { findPlayer(game.players, it) }
                    ?: game.currentPlayer
                    ?: throw CLIException("cli.player.current_missing")
            context.printLine("cli.player.info", player)
          }
        },
        object : Command("listCards", CLIMode.GAME) {
          override fun execute(arguments: List<String>, context: CommandContext) {
            context.printLine("cli.cards.list", context.catalog.getCardList())
          }
        },
        object : Command("buyCard", CLIMode.GAME) {
          override fun execute(arguments: List<String>, context: CommandContext) {
            require(arguments.size == 1) { "buyCard <cardid>" }
            val game = requireGame()
            val player = currentPlayer(game)
            game.driver.buyCard(player, arguments.single())
          }
        },
        object : Command("pickPlayer", CLIMode.GAME) {
          override fun execute(arguments: List<String>, context: CommandContext) {
            require(arguments.size == 1) { "pickPlayer <playername>" }
            val game = requireGame()
            val player = currentPlayer(game)
            val target = findPlayer(game.driver.game.players, arguments.single())
            game.driver.pickAndChargePlayer(player, target)
          }
        },
        object : Command("swap", CLIMode.GAME) {
          override fun execute(arguments: List<String>, context: CommandContext) {
            require(arguments.size == 2) { "swap <playername> <cardId>" }
            val game = requireGame()
            val player = currentPlayer(game)
            val opponent = findPlayer(game.driver.game.players, arguments[0])
            val awaiting = game.driver.game.inputEffects.peek() as? SwapCardsInputEffect
                ?: throw CLIException("cli.swap.unexpected")
            val fromCard =
                opponent.cards.firstOrNull { it.cardId == arguments[1] }
                    ?: throw CLIException("cli.swap.no_card", arguments[1])
            val toCard =
                player.cards.firstOrNull { it.type != ru.kislball.machikoro.cards.common.CardType.SIGHT }
                    ?: throw CLIException("cli.swap.no_own_card")
            game.driver.swapCards(player, SwapCardsInput(opponent, fromCard, toCard))
          }
        },
        object : Command("rethrow", CLIMode.GAME) {
          override fun execute(arguments: List<String>, context: CommandContext) {
            val game = requireGame()
            game.driver.submitRethrowDecision(currentPlayer(game), true)
          }
        },
        object : Command("addTwo", CLIMode.GAME) {
          override fun execute(arguments: List<String>, context: CommandContext) {
            val game = requireGame()
            val step = game.driver.game.currentStepPhase as? PendingStepPhase
                ?: throw IllegalArgumentException("No active step")
            step.results.getOrNull<DiceRollResult>()?.let {
              step.results.set(it.copy(diceThrown = it.diceThrown.map { value -> value + 2 }))
              return
            }
            step.results.getOrNull<IntermediateRollResult>()?.let {
              step.results.set(IntermediateRollResult(it.result.copy(diceThrown = it.result.diceThrown.map { value -> value + 2 })))
              return
            }
            throw CLIException("cli.dice.missing")
          }
        },
      )
  }

  private fun ReactiveGame.withObserver(context: CommandContext): ReactiveGame {
    driver.observeEffects { effect, _ ->
      effectLog.add(context.localiser.localise("cli.effect", effect))
    }
    return this
  }

  private fun requireGame(): ReactiveGame {
    return session.activeGame ?: throw CLIException("cli.game.not_active")
  }

  private fun currentPlayer(game: ReactiveGame): Player {
    return game.driver.game.currentPlayer ?: throw CLIException("cli.player.current_missing")
  }

  private fun findPlayer(players: List<Player>, name: String): Player {
    return players.firstOrNull { it.name == name } ?: throw CLIException("cli.player.not_found", name)
  }
}
