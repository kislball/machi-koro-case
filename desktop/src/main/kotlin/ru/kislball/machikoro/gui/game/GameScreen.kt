package ru.kislball.machikoro.gui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import ru.kislball.machikoro.gui.AppViewModel
import ru.kislball.machikoro.gui.game.composables.GameLogPanel
import ru.kislball.machikoro.gui.game.composables.PlayerDisplay
import ru.kislball.machikoro.gui.game.composables.PlayerDisplayAlignment
import ru.kislball.machikoro.gui.game.composables.PlayerDisplaySelectableState
import ru.kislball.machikoro.gui.game.composables.toAlignment
import ru.kislball.machikoro.gui.localisation.LocalAppLocaliser

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
  val eventLog = gameViewModel.eventLog
  val eventLogTitle = gameViewModel.eventLogTitle
  val pendingInputMarker = gameViewModel.pendingInputMarker
  val localiser = LocalAppLocaliser.current

  Box(
      modifier = Modifier.fillMaxSize(),
  ) {
    IconButton(
        onClick = {
          gameViewModel.saveGame()
          onLeave()
        },
        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).align(Alignment.TopStart)) {
          Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = localiser.localise("gui.action.back"))
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
          selectable =
              when {
                gameViewModel.isSelectablePickTarget(player) -> PlayerDisplaySelectableState.Player
                gameViewModel.isSelectableSwapCardOwner(player) ->
                    PlayerDisplaySelectableState.Cards
                else -> PlayerDisplaySelectableState.None
              },
          onSelect = {
            if (gameViewModel.isSelectablePickTarget(player)) {
              gameViewModel.submitPickPlayer(player)
            }
          },
          onCardSelected = { card -> gameViewModel.selectSwapCard(player, card) },
          isCardSelectable = { card -> gameViewModel.isSelectableSwapCard(player, card) },
      )
    }
    GamePrompts(
        gameViewModel = gameViewModel,
        currentPlayer = game.currentPlayer,
        buyPromptDelayMs = buyPromptDelayMs,
        modifier = Modifier.align(Alignment.Center),
    )
    GameLogPanel(
        title = eventLogTitle,
        entries = eventLog,
        pendingInputMarker = pendingInputMarker,
        modifier = Modifier.align(Alignment.TopEnd))
  }
}
