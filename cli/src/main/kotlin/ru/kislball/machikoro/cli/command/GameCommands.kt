package ru.kislball.machikoro.cli.command

import ru.kislball.machikoro.cards.common.CardType
import ru.kislball.machikoro.cli.error.CLIException
import ru.kislball.machikoro.cli.session.ActiveCliGame
import ru.kislball.machikoro.cli.session.CLIMode
import ru.kislball.machikoro.cli.session.CLISession
import ru.kislball.machikoro.effects.cards.buy.BuyCardInputEffect
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInput
import ru.kislball.machikoro.effects.cards.buy.BuyCardInputEffect
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInputEffect
import ru.kislball.machikoro.exceptions.CardNotFoundException
import ru.kislball.machikoro.exceptions.InvalidSaveNameException
import ru.kislball.machikoro.exceptions.PlayerNotFoundException
import ru.kislball.machikoro.exceptions.SaveNotFoundException
import ru.kislball.machikoro.exceptions.UnknownCatalogException
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.IntermediateRollResult
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.PendingStepPhase
import ru.kislball.machikoro.game.utilities.getOrNull

internal fun gameCommands(session: CLISession): List<Command> {
  return listOf(
      object : Command("exit", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          session.activeGame = null
          session.mode = CLIMode.MANAGEMENT
          context.printLine("cli.game.exited")
        }
      },
      object : Command("admin.give-card", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          val game = checkNotNull(context.session.activeGame?.driver?.game)
          require(arguments.size == 2) { "admin.give-card <user> <card>" }
          val player =
              game.players.find { it.name == arguments[0] }
                  ?: throw PlayerNotFoundException(arguments[0])
          val card = game.catalog[arguments[1]] ?: throw CardNotFoundException(arguments[1])
          player.cards.add(card)
        }
      },
      object : Command("admin.remove-card", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          val game = checkNotNull(context.session.activeGame?.driver?.game)
          require(arguments.size == 2) { "admin.give-card <user> <card>" }
          val player =
              game.players.find { it.name == arguments[0] }
                  ?: throw PlayerNotFoundException(arguments[0])
          val card = game.catalog[arguments[1]] ?: throw CardNotFoundException(arguments[1])
          player.cards.remove(card)
        }
      },
      object : Command("save", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          require(arguments.size == 1) { "save <name>" }
          val game = requireGame(session)
          withStorageErrors {
            context.storage.save(arguments.single(), game.driver, game.catalogId)
          }
          context.printLine("cli.games.saved", arguments.single())
        }
      },
      object : Command("info", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          val game = requireGame(session).driver.game
          val player =
              arguments.singleOrNull()?.let { findPlayer(game.players, it) }
                  ?: game.currentPlayer
                  ?: throw CLIException("cli.player.current_missing")
          context.printLine("cli.player.info", player)
        }
      },
      object : Command("listCards", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          context.printLine(
              "cli.cards.list", requireGame(session).driver.game.catalog.getCardList())
        }
      },
      object : Command("buyCard", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          require(arguments.size == 1) { "buyCard <cardid>" }
          val game = requireGame(session)
          val player = currentPlayer(game)
          game.driver.game.inputEffects.peek() as? BuyCardInputEffect
              ?: throw CLIException("cli.buy.unexpected")
          game.driver.buyCard(player, arguments.single())
        }
      },
      object : Command("skipBuy", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          val game = requireGame(session)
          val player = currentPlayer(game)
          game.driver.game.inputEffects.peek() as? BuyCardInputEffect
              ?: throw CLIException("cli.buy.unexpected")
          game.driver.skipCardPurchase(player)
        }
      },
      object : Command("roll", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          require(arguments.size == 1) { "roll <1|2>" }
          val game = requireGame(session)
          val player = currentPlayer(game)
          game.driver.rollDice(player, arguments.single().toInt())
        }
      },
      object : Command("pickPlayer", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          require(arguments.size == 1) { "pickPlayer <playername>" }
          val game = requireGame(session)
          val player = currentPlayer(game)
          val target = findPlayer(game.driver.game.players, arguments.single())
          game.driver.pickAndChargePlayer(player, target)
        }
      },
      object : Command("swap", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          require(arguments.size == 3) { "swap <playername> <theirCardId> <yourCardId>" }
          val game = requireGame(session)
          val player = currentPlayer(game)
          val opponent = findPlayer(game.driver.game.players, arguments[0])
          game.driver.game.inputEffects.peek() as? SwapCardsInputEffect
              ?: throw CLIException("cli.swap.unexpected")
          val fromCard =
              opponent.cards.firstOrNull { it.cardId == arguments[1] }
                  ?: throw CLIException("cli.swap.no_card", arguments[1])
          val toCard =
              player.cards.firstOrNull { it.cardId == arguments[2] }
                  ?: throw CLIException("cli.swap.no_own_card", arguments[2])
          require(toCard.type != CardType.SIGHT) { "swap <playername> <theirCardId> <yourCardId>" }
          game.driver.swapCards(player, SwapCardsInput(opponent, fromCard, toCard))
        }
      },
      object : Command("rethrow", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          val game = requireGame(session)
          game.driver.submitRethrowDecision(currentPlayer(game), true)
        }
      },
      object : Command("keep", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          val game = requireGame(session)
          game.driver.submitRethrowDecision(currentPlayer(game), false)
        }
      },
      object : Command("takeAdditionalStep", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          val game = requireGame(session)
          game.driver.submitAdditionalStepDecision(currentPlayer(game), true)
        }
      },
      object : Command("skipAdditionalStep", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          val game = requireGame(session)
          game.driver.submitAdditionalStepDecision(currentPlayer(game), false)
        }
      },
      object : Command("addTwo", CLIMode.GAME) {
        override fun execute(arguments: List<String>, context: CommandContext) {
          val game = requireGame(session)
          val step =
              game.driver.game.currentStepPhase as? PendingStepPhase
                  ?: throw IllegalArgumentException("No active step")
          step.results.getOrNull<DiceRollResult>()?.let {
            step.results.set(it.copy(diceThrown = it.diceThrown.map { value -> value + 2 }))
            return
          }
          step.results.getOrNull<IntermediateRollResult>()?.let {
            step.results.set(
                IntermediateRollResult(
                    it.result.copy(diceThrown = it.result.diceThrown.map { value -> value + 2 })))
            return
          }
          throw CLIException("cli.dice.missing")
        }
      },
  )
}

private fun requireGame(session: CLISession): ActiveCliGame {
  return session.activeGame ?: throw CLIException("cli.game.not_active")
}

private fun currentPlayer(game: ActiveCliGame): Player {
  return game.driver.game.currentPlayer ?: throw CLIException("cli.player.current_missing")
}

private fun findPlayer(players: List<Player>, name: String): Player {
  return players.firstOrNull { it.name == name } ?: throw CLIException("cli.player.not_found", name)
}

inline fun <T> withStorageErrors(action: () -> T): T {
  return try {
    action()
  } catch (exception: RuntimeException) {
    val mapped =
        when (exception) {
          is SaveNotFoundException ->
              CLIException("cli.games.not_found", exception.saveName, exception)
          is UnknownCatalogException ->
              CLIException("cli.catalog.not_found", exception.catalogId, exception)
          is InvalidSaveNameException -> CLIException("cli.games.invalid_name", cause = exception)
          else -> throw exception
        }
    throw mapped
  }
}
