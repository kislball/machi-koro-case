package ru.kislball.machikoro.gui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.kislball.machikoro.cards.common.CardCatalogDefinition
import ru.kislball.machikoro.cards.common.CardCatalogResolver
import ru.kislball.machikoro.cards.common.catalogId
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.facility.GameFactory
import ru.kislball.machikoro.gui.localisation.russianDesktopLocaliser
import ru.kislball.machikoro.localisation.Localiser
import ru.kislball.machikoro.storage.GameStorage
import ru.kislball.machikoro.storage.GameStorageFactory
import ru.kislball.machikoro.storage.SavedGameSummary
import ru.kislball.machikoro.storage.TopEntry

data class ActiveGameSession(
    val id: String,
)

data class AppUiState(
    val currentScreen: Screen = Screen.GameSelection,
    val currentGame: ActiveGameSession? = null,
    val savedGames: List<SavedGameSummary> = emptyList(),
    val localiser: Localiser = russianDesktopLocaliser(),
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

  fun top(): List<TopEntry> {
    return storage.top()
  }

  fun openTop() {
    uiState =
        uiState.copy(
            currentScreen = Screen.TopPlayers,
            currentGame = null,
            savedGames = storage.list(),
        )
  }

  fun saveGame(id: String, catalogId: String, driver: GameDriver) {
    storage.save(id, driver, catalogId)
  }

  fun saveGame(id: String, driver: GameDriver) {
    saveGame(id, driver.game.catalog.catalogId, driver)
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
      val root = "${System.getProperty("user.dir")}/.machikoro-cli"
      return GameStorageFactory.json(
          root,
          STANDARD_CATALOG_ID,
          CardCatalogResolver(CardCatalogDefinition(STANDARD_CATALOG_ID, StandardCatalog)),
      )
    }
  }
}
