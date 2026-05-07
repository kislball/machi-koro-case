package ru.kislball.machikoro.gui

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import ru.kislball.machikoro.cards.common.CardCatalogResolver
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.gui.localisation.russianDesktopLocaliser
import ru.kislball.machikoro.storage.GameStorage
import ru.kislball.machikoro.storage.SavedGameSummary
import ru.kislball.machikoro.storage.StorageBackend
import ru.kislball.machikoro.storage.StoredGame

class AppViewModelStorageTest {
  @Test
  fun `switching storage returns to game selection and refreshes save list`() {
    val jsonStorage = FakeStorage(listOf(summary("json-save")))
    val sqlStorage = FakeStorage(listOf(summary("sql-save")))
    val viewModel =
        AppViewModel(
            storageFactory = { backend ->
              when (backend) {
                StorageBackend.JSON -> jsonStorage
                StorageBackend.SQL -> sqlStorage
              }
            })

    assertEquals(StorageBackend.JSON, viewModel.uiState.storageBackend)
    assertEquals(listOf("json-save"), viewModel.uiState.savedGames.map { it.name })

    viewModel.openGame("json-save")
    viewModel.switchStorage(StorageBackend.SQL)

    assertEquals(Screen.GameSelection, viewModel.uiState.currentScreen)
    assertNull(viewModel.uiState.currentGame)
    assertEquals(StorageBackend.SQL, viewModel.uiState.storageBackend)
    assertEquals(listOf("sql-save"), viewModel.uiState.savedGames.map { it.name })

    viewModel.toggleStorage()

    assertEquals(StorageBackend.JSON, viewModel.uiState.storageBackend)
    assertEquals(listOf("json-save"), viewModel.uiState.savedGames.map { it.name })
  }

  @Test
  fun `storage labels are localized`() {
    val localiser = russianDesktopLocaliser()

    assertEquals("JSON", localiser.localise("gui.storage.json"))
    assertEquals("SQL", localiser.localise("gui.storage.sql"))
    assertEquals("Хранилище: SQL", localiser.localise("gui.storage.current", "SQL"))
  }

  private fun summary(name: String): SavedGameSummary {
    return SavedGameSummary(
        name = name,
        playerNames = listOf("alice", "bob"),
        createdAt = Instant.EPOCH,
        finished = false,
        winnerName = null,
        catalogId = "standard",
    )
  }

  private class FakeStorage(
      private val saves: List<SavedGameSummary>,
  ) : GameStorage("standard", CardCatalogResolver.default) {
    override fun save(name: String, game: GameDriver, catalogId: String) {
      error("save should not be called")
    }

    override fun load(name: String): StoredGame {
      error("load should not be called")
    }

    override fun list(): List<SavedGameSummary> {
      return saves
    }

    override fun delete(name: String) {
      error("delete should not be called")
    }
  }
}
