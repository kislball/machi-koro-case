package ru.kislball.machikoro.gui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.kislball.machikoro.gui.screen.Screen
import ru.kislball.machikoro.gui.screen.management.GameSelectionScreen
import ru.kislball.machikoro.gui.screen.management.ManagementViewModel

@Composable
fun App() {
  var currentScreen by remember { mutableStateOf(Screen.GameSelection) }
  val managementViewModel = remember { ManagementViewModel() }

  MaterialTheme {
    Column(
        modifier = Modifier.fillMaxSize().safeContentPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
      when (currentScreen) {
        Screen.GameSelection -> GameSelectionScreen(managementViewModel)
        Screen.Game -> TODO()
      }
    }
  }
}
