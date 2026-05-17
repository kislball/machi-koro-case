package ru.kislball.machikoro.storage

import java.io.File
import org.jetbrains.exposed.v1.jdbc.Database
import ru.kislball.machikoro.cards.common.CardCatalogResolver
import ru.kislball.machikoro.storage.json.JSONExporter
import ru.kislball.machikoro.storage.json.JSONImporter
import ru.kislball.machikoro.storage.json.JsonGameStorage
import ru.kislball.machikoro.storage.sql.SQLStorage

object GameStorageFactory {
  fun create(
      backend: StorageBackend,
      root: String,
      defaultCatalogId: String,
      catalogResolver: CardCatalogResolver,
  ): GameStorage {
    return when (backend) {
      StorageBackend.JSON -> json(root, defaultCatalogId, catalogResolver)
      StorageBackend.SQL -> h2("$root/machikoro", defaultCatalogId, catalogResolver)
    }
  }

  fun json(
      root: String,
      defaultCatalogId: String,
      catalogResolver: CardCatalogResolver,
      fileExtension: String = "json",
  ): GameStorage {
    return JsonGameStorage(
        root = root,
        defaultCatalogId = defaultCatalogId,
        catalogResolver = catalogResolver,
        importer = JSONImporter(),
        exporter = JSONExporter(),
        fileExtension = fileExtension,
    )
  }

  fun h2(
      databasePath: String,
      defaultCatalogId: String,
      catalogResolver: CardCatalogResolver,
  ): GameStorage {
    File(databasePath).parentFile?.mkdirs()
    Database.connect(
        url = "jdbc:h2:file:$databasePath",
        driver = "org.h2.Driver",
    )
    return SQLStorage(defaultCatalogId, catalogResolver)
  }
}
