package ru.kislball.machikoro.gui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.nio.file.Files
import java.nio.file.Path
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.facility.GameFactory
import ru.kislball.machikoro.storage.GameStorage
import ru.kislball.machikoro.storage.SavedGameSummary

data class ActiveGameSession(
    val id: String,
)

data class AppUiState(
    val currentScreen: Screen = Screen.GameSelection,
    val currentGame: ActiveGameSession? = null,
    val savedGames: List<SavedGameSummary> = emptyList(),
) {
  init {
    require(currentScreen != Screen.Game || currentGame != null) {
      "currentGame must not be null when currentScreen is Game"
    }
  }
}

class AppViewModel(
    private val storage: GameStorage = defaultStorage(),
) {
  var uiState by mutableStateOf(AppUiState())
    private set

  init {
    refreshSavedGames()
  }

  fun loadGame(id: String): GameDriver {
    return storage.load(id).driver
  }

  fun createGame(name: String, players: List<String>) {
    val gameName = name.trim()
    val driver = GameFactory.createDriver(StandardCatalog, players)
    storage.save(gameName, driver, STANDARD_CATALOG_ID)
    uiState =
        uiState.copy(
            currentScreen = Screen.Game,
            currentGame = ActiveGameSession(gameName),
            savedGames = storage.list(),
        )
  }

  fun openGame(gameId: String) {
    uiState =
        uiState.copy(
            currentScreen = Screen.Game,
            currentGame = ActiveGameSession(gameId),
        )
  }

  fun openManagement() {
    uiState =
        uiState.copy(
            currentScreen = Screen.GameSelection,
            currentGame = null,
            savedGames = storage.list(),
        )
  }

  fun refreshSavedGames() {
    uiState = uiState.copy(savedGames = storage.list())
  }

  companion object {
    private const val STANDARD_CATALOG_ID = "standard"

    private fun defaultStorage(): GameStorage {
      val root = Path.of(System.getProperty("user.dir"), ".machikoro-cli")
      Files.createDirectories(root)
      return GameStorage(root, STANDARD_CATALOG_ID) { StandardCatalog }
    }
  }
}
