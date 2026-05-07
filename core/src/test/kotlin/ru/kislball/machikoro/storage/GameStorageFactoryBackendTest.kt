@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

package ru.kislball.machikoro.storage

import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.kislball.machikoro.cards.common.CardCatalogResolver
import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.facility.GameFactory

class GameStorageFactoryBackendTest {
  private val tempDir = createTempDirectory("machikoro-storage-factory")

  @AfterTest
  fun tearDown() {
    tempDir.deleteRecursively()
  }

  @Test
  fun `create keeps json and h2 sql saves in separate backends`() {
    val jsonStorage = storageFor(StorageBackend.JSON)
    val sqlStorage = storageFor(StorageBackend.SQL)

    sqlStorage.save(
        "sql-save",
        GameFactory.createDriver(StandardCatalog, listOf("alice", "bob")),
        "standard",
    )

    assertEquals(listOf("sql-save"), sqlStorage.list().map { it.name })
    assertTrue(jsonStorage.list().isEmpty())
    assertTrue(tempDir.resolve("machikoro.mv.db").exists())
  }

  @Test
  fun `h2 sql storage created by factory persists saves for a later instance`() {
    val first = storageFor(StorageBackend.SQL)
    first.save(
        "persistent-save",
        GameFactory.createDriver(StandardCatalog, listOf("alice", "bob")),
        "standard",
    )

    val second = storageFor(StorageBackend.SQL)

    assertEquals(listOf("persistent-save"), second.list().map { it.name })
    assertEquals(
        listOf("alice", "bob"), second.load("persistent-save").driver.game.players.map { it.name })
  }

  private fun storageFor(backend: StorageBackend): GameStorage {
    return GameStorageFactory.create(
        backend,
        tempDir.toString(),
        "standard",
        CardCatalogResolver.default,
    )
  }
}
