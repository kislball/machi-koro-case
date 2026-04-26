@file:Suppress("FunctionNaming")

package ru.kislball.machikoro.gui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import ru.kislball.machikoro.gui.AppViewModel
import ru.kislball.machikoro.gui.game.prompts.DiceInputPrompt

@Composable
fun GameScreen(
    currentGameId: String,
    app: AppViewModel,
    onLeave: () -> Unit,
) {
  val gameViewModel = remember { GameViewModel(app, currentGameId) }
  val driver = gameViewModel.driver
  val game = driver.game
  val players = game.players
  val currentStep = game.currentStepPhase
  val currentDiceResult = gameViewModel.currentDiceResult
  val shouldPromptDiceChoice = gameViewModel.shouldPromptDiceChoice
  val eventLog = gameViewModel.eventLog
  val eventLogTitle = gameViewModel.eventLogTitle
  val pendingInputMarker = gameViewModel.pendingInputMarker

  LaunchedEffect(currentStep, game.finished, shouldPromptDiceChoice, currentDiceResult) {
    gameViewModel.advanceGame()
  }

  Box(
      modifier = Modifier.fillMaxSize(),
  ) {
    Box(Modifier.align(Alignment.Center)) {
      when {
        shouldPromptDiceChoice -> {
          DiceInputPrompt(
              canRollTwoDice = true,
              playerName = currentStep?.currentPlayer?.name ?: players.first().name,
              onSelect = gameViewModel::submitDiceChoice)
        }
        currentDiceResult != null -> {
          DiceRoll(currentDiceResult.diceThrown)
        }
        else -> {
          Text("Rolling...")
        }
      }
    }
    IconButton(
        onClick = onLeave,
        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).align(Alignment.TopStart)) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Leave")
        }
    GameLogPanel(
        title = eventLogTitle,
        entries = eventLog,
        pendingInputMarker = pendingInputMarker,
        modifier = Modifier.align(Alignment.TopEnd))

    val alignments =
        listOf(
            PlayerDisplayAlignment.Left,
            PlayerDisplayAlignment.Bottom,
            PlayerDisplayAlignment.Right,
            PlayerDisplayAlignment.Top,
        )
    players.forEachIndexed { index, player ->
      val alignment = alignments[index % 4]
      PlayerDisplay(
          player.name,
          player.balance,
          player.cards,
          alignment = alignment,
          modifier = Modifier.align(alignment.toAlignment()),
          isCurrent = game.currentPlayer == player)
    }
  }
}
