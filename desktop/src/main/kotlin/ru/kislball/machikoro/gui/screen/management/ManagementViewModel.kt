package ru.kislball.machikoro.gui.screen.management

import java.nio.file.Files
import java.nio.file.Path
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.facility.GameFactory
import ru.kislball.machikoro.storage.GameStorage
import ru.kislball.machikoro.storage.SavedGameSummary

data class ManagementUiState(
    val savedGames: List<SavedGameSummary> = emptyList(),
)

class ManagementViewModel(
    private val storage: GameStorage = defaultStorage(),
) {
  var uiState by mutableStateOf(ManagementUiState())
    private set

  init {
    refresh()
  }

  fun createGame(name: String, players: List<String>) {
    val gameName = name.trim()
    val driver = GameFactory.createDriver(StandardCatalog, players)
    storage.save(gameName, driver, STANDARD_CATALOG_ID)
    refresh()
  }

  fun refresh() {
    uiState = ManagementUiState(savedGames = storage.list())
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
