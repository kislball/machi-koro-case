package ru.kislball.machikoro.storage

import ru.kislball.machikoro.cards.common.CardCatalogResolver
import ru.kislball.machikoro.facility.json.JSONExporter
import ru.kislball.machikoro.facility.json.JSONImporter

object GameStorageFactory {
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
}
