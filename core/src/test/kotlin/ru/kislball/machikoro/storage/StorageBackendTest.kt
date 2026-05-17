package ru.kislball.machikoro.storage

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StorageBackendTest {
  @Test
  fun `backend parser accepts cli names case insensitively`() {
    assertEquals(StorageBackend.JSON, StorageBackend.parse("json"))
    assertEquals(StorageBackend.JSON, StorageBackend.parse("JSON"))
    assertEquals(StorageBackend.SQL, StorageBackend.parse("sql"))
    assertEquals(StorageBackend.SQL, StorageBackend.parse("SQL"))
  }

  @Test
  fun `backend parser rejects unknown storage names`() {
    assertNull(StorageBackend.parse("xml"))
    assertNull(StorageBackend.parse(""))
  }

  @Test
  fun `next toggles between json and sql`() {
    assertEquals(StorageBackend.SQL, StorageBackend.JSON.next())
    assertEquals(StorageBackend.JSON, StorageBackend.SQL.next())
  }
}
