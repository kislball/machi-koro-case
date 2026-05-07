package ru.kislball.machikoro.storage

enum class StorageBackend(val cliName: String) {
  JSON("json"),
  SQL("sql");

  fun next(): StorageBackend {
    return when (this) {
      JSON -> SQL
      SQL -> JSON
    }
  }

  companion object {
    fun parse(raw: String): StorageBackend? {
      return entries.firstOrNull { it.cliName == raw.lowercase() }
    }
  }
}
