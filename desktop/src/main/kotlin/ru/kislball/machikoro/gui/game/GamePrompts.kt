package ru.kislball.machikoro.gui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.gui.composables.DiceRoll
import ru.kislball.machikoro.gui.game.prompts.AdditionalStepPrompt
import ru.kislball.machikoro.gui.game.prompts.BuyCardOptionUi
import ru.kislball.machikoro.gui.game.prompts.BuyCardPrompt
import ru.kislball.machikoro.gui.game.prompts.DiceInputPrompt
import ru.kislball.machikoro.gui.game.prompts.RethrowPrompt

@Composable
fun GamePrompts(
    gameViewModel: GameViewModel,
    currentPlayer: Player?,
    buyPromptDelayMs: Long,
    modifier: Modifier = Modifier,
) {
  val currentStep = gameViewModel.driver.game.currentStepPhase
  val currentDiceResult = gameViewModel.currentDiceResult
  val shouldPromptDiceChoice = gameViewModel.shouldPromptDiceChoice
  val shouldPromptRethrow = gameViewModel.shouldPromptRethrow
  val shouldPromptBuyCard = gameViewModel.shouldPromptBuyCard
  val shouldPromptPickPlayer = gameViewModel.shouldPromptPickPlayer
  val shouldPromptAdditionalStep = gameViewModel.shouldPromptAdditionalStep
  val shouldPromptSwapCards = gameViewModel.shouldPromptSwapCards
  val winner = gameViewModel.winner
  val localiser = gameViewModel.localiser
  var buyPromptVisible by remember(gameViewModel.gameId) { mutableStateOf(false) }

  LaunchedEffect(shouldPromptBuyCard, currentDiceResult, currentStep?.stepNumber) {
    if (!shouldPromptBuyCard) {
      buyPromptVisible = false
      return@LaunchedEffect
    }

    buyPromptVisible = false
    if (currentDiceResult != null) {
      delay(buyPromptDelayMs)
    }
    buyPromptVisible = true
  }

  LaunchedEffect(
      currentStep,
      gameViewModel.driver.game.finished,
      shouldPromptDiceChoice,
      shouldPromptRethrow,
      shouldPromptBuyCard,
      shouldPromptAdditionalStep,
      shouldPromptPickPlayer,
      shouldPromptSwapCards,
      currentDiceResult) {
        gameViewModel.advanceGame()
      }

  Box(modifier) {
    val player = currentPlayer ?: return
    when {
      winner != null -> {
        Card(
            modifier = modifier.padding(16.dp),
        ) {
          Column {
            Text(
                localiser.localise("gui.game.finished.title"),
                fontSize = 16.sp,
                fontWeight = FontWeight.W500)
            Spacer(Modifier.height(16.dp))
            Text(localiser.localise("gui.game.finished.winner", winner))
          }
        }
      }
      shouldPromptDiceChoice -> {
        DiceInputPrompt(
            canRollTwoDice = true,
            playerName = player.name,
            onSelect = gameViewModel::submitDiceChoice)
      }
      shouldPromptRethrow -> {
        RethrowPrompt(
            playerName = player.name,
            onSelect = gameViewModel::submitRethrowDecision,
        )
      }
      shouldPromptBuyCard && buyPromptVisible -> {
        BuyCardPrompt(
            options =
                gameViewModel.buyCardOptions.map { option ->
                  BuyCardOptionUi(
                      card = option.card,
                      cardId = option.card.cardId,
                      title = option.title,
                      priceLabel = localiser.localise("gui.game.prompt.buy.price", option.price),
                      remainingLabel =
                          localiser.localise(
                              "gui.game.prompt.buy.remaining", option.remainingCopies),
                      enabled = option.enabled,
                  )
                },
            title = localiser.localise("gui.game.prompt.buy.title", player.name),
            description = localiser.localise("gui.game.prompt.buy.description"),
            skipLabel = localiser.localise("gui.game.prompt.buy.skip"),
            onSelect = gameViewModel::submitCardPurchase,
            onSkip = gameViewModel::skipCardPurchase)
      }
      shouldPromptPickPlayer -> {
        Card { Text(localiser.localise("gui.game.prompt.pick_player")) }
      }
      shouldPromptSwapCards -> {
        Card { Text(checkNotNull(gameViewModel.swapPromptText)) }
      }
      shouldPromptAdditionalStep -> {
        AdditionalStepPrompt(
            playerName = player.name,
            onSelect = gameViewModel::submitAdditionalStep,
        )
      }
      currentDiceResult != null -> {
        Row { DiceRoll(currentDiceResult.diceThrown) }
      }
      else -> {
        Text(localiser.localise("gui.game.prompt.rolling"))
      }
    }
  }
}
