package ru.kislball.machikoro.gui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.kislball.machikoro.gui.game.GameScreen
import ru.kislball.machikoro.gui.localisation.LocalAppLocaliser
import ru.kislball.machikoro.gui.management.GameSelectionScreen
import ru.kislball.machikoro.gui.top.TopPlayers

@Composable
fun App(appViewModel: AppViewModel = remember { AppViewModel() }) {
  val uiState = appViewModel.uiState

  MaterialTheme {
    CompositionLocalProvider(LocalAppLocaliser provides uiState.localiser) {
      Column(
          modifier = Modifier.fillMaxSize().safeContentPadding(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
      ) {
        when (uiState.currentScreen) {
          Screen.GameSelection -> GameSelectionScreen(uiState = uiState, app = appViewModel)
          Screen.Game ->
              GameScreen(
                  currentGameId = requireNotNull(uiState.currentGame).id,
                  app = appViewModel,
                  onLeave = appViewModel::openManagement,
              )
          Screen.TopPlayers ->
              TopPlayers(
                  appViewModel.top(),
                  onReturn = appViewModel::openManagement,
              )
        }
      }
    }
  }
}
