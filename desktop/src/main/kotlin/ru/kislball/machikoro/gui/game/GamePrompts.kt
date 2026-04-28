package ru.kislball.machikoro.gui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
            playerName = player.name,
            options =
                gameViewModel.buyCardOptions.map { option ->
                  BuyCardOptionUi(
                      card = option.card,
                      cardId = option.card.cardId,
                      title = option.title,
                      priceLabel = "Цена: ${option.price}",
                      remainingLabel = "Осталось: ${option.remainingCopies}",
                      enabled = option.enabled,
                  )
                },
            onSelect = gameViewModel::submitCardPurchase,
            onSkip = gameViewModel::skipCardPurchase)
      }
      shouldPromptPickPlayer -> {
        Card { Text("Выберите игрока, с которого хотите взять деньги") }
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
        Text("Rolling...")
      }
    }
  }
}
