package ru.kislball.machikoro.gui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import kotlinx.coroutines.delay
import ru.kislball.machikoro.gui.AppViewModel
import ru.kislball.machikoro.gui.composables.DiceRoll
import ru.kislball.machikoro.gui.game.composables.GameLogPanel
import ru.kislball.machikoro.gui.game.composables.PlayerDisplay
import ru.kislball.machikoro.gui.game.composables.PlayerDisplayAlignment
import ru.kislball.machikoro.gui.game.composables.toAlignment
import ru.kislball.machikoro.gui.game.prompts.BuyCardOptionUi
import ru.kislball.machikoro.gui.game.prompts.BuyCardPrompt
import ru.kislball.machikoro.gui.game.prompts.DiceInputPrompt

@Composable
fun GameScreen(
    currentGameId: String,
    app: AppViewModel,
    onLeave: () -> Unit,
) {
  val buyPromptDelayMs = 1_000L
  val gameViewModel = remember { GameViewModel(app, currentGameId) }
  val driver = gameViewModel.driver
  val game = driver.game
  val players = game.players
  val currentStep = game.currentStepPhase
  val currentDiceResult = gameViewModel.currentDiceResult
  val shouldPromptDiceChoice = gameViewModel.shouldPromptDiceChoice
  val shouldPromptBuyCard = gameViewModel.shouldPromptBuyCard
  val shouldPromptPickPlayer = gameViewModel.shouldPromptPickPlayer
  val eventLog = gameViewModel.eventLog
  val eventLogTitle = gameViewModel.eventLogTitle
  val pendingInputMarker = gameViewModel.pendingInputMarker
  var buyPromptVisible by remember(currentGameId) { mutableStateOf(false) }

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
      currentStep, game.finished, shouldPromptDiceChoice, shouldPromptBuyCard, shouldPromptPickPlayer, currentDiceResult) {
        gameViewModel.advanceGame()
      }

  Box(
      modifier = Modifier.fillMaxSize(),
  ) {
    IconButton(
        onClick = {
          gameViewModel.saveGame()
          onLeave()
        },
        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).align(Alignment.TopStart)) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Leave")
        }

    val alignments =
        listOf(
            PlayerDisplayAlignment.Left,
            PlayerDisplayAlignment.Bottom,
            PlayerDisplayAlignment.Right,
            PlayerDisplayAlignment.Top,
        )
    players.forEachIndexed { index, player ->
      val alignment = alignments[index % alignments.size]
      PlayerDisplay(
          player.name,
          player.balance,
          player.cards,
          alignment = alignment,
          modifier = Modifier.align(alignment.toAlignment()),
          isCurrent = game.currentPlayer == player,
          selectable = shouldPromptPickPlayer && game.currentPlayer != player,
          onSelect = {
            if (shouldPromptPickPlayer) {
              gameViewModel.submitPickPlayer(player)
            }
          },
      )
    }
    Box(Modifier.align(Alignment.Center)) {
      when {
        shouldPromptDiceChoice -> {
          DiceInputPrompt(
              canRollTwoDice = true,
              playerName = currentStep?.currentPlayer?.name ?: players.first().name,
              onSelect = gameViewModel::submitDiceChoice)
        }
        shouldPromptBuyCard && buyPromptVisible -> {
          BuyCardPrompt(
              playerName = currentStep?.currentPlayer?.name ?: players.first().name,
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
        currentDiceResult != null -> {
          Row { DiceRoll(currentDiceResult.diceThrown) }
        }
        else -> {
          Text("Rolling...")
        }
      }
    }
    GameLogPanel(
        title = eventLogTitle,
        entries = eventLog,
        pendingInputMarker = pendingInputMarker,
        modifier = Modifier.align(Alignment.TopEnd))
  }
}
