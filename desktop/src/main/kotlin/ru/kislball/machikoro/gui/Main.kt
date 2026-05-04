package ru.kislball.machikoro.gui

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
  val appViewModel = remember { AppViewModel() }
  val localiser = appViewModel.uiState.localiser
  Window(
      onCloseRequest = ::exitApplication,
      title = localiser.localise("gui.app.title"),
  ) {
    App(appViewModel)
  }
}
