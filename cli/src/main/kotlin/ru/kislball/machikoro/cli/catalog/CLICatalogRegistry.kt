package ru.kislball.machikoro.cli.catalog

import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.common.CardCatalogDefinition
import ru.kislball.machikoro.cards.common.CardCatalogResolver
import ru.kislball.machikoro.cards.standard.StandardCatalog

data class CLICatalogDefinition(
    val id: String,
    val catalog: CardCatalog,
)

class CLICatalogRegistry(
    definitions: List<CLICatalogDefinition>,
    val defaultCatalogId: String,
) {
  private val definitionsById = definitions.associateBy { it.id }

  val resolver = CardCatalogResolver(definitions.map { CardCatalogDefinition(it.id, it.catalog) })

  init {
    require(definitionsById.size == definitions.size) { "Catalog ids must be unique" }
    require(defaultCatalogId in definitionsById) { "Default catalog must be registered" }
  }

  fun get(id: String): CLICatalogDefinition? = definitionsById[id]

  fun require(id: String): CLICatalogDefinition {
    return checkNotNull(get(id)) { "Unknown catalog: $id" }
  }

  companion object {
    fun default(): CLICatalogRegistry {
      return CLICatalogRegistry(
          definitions = listOf(CLICatalogDefinition("standard", StandardCatalog)),
          defaultCatalogId = "standard",
      )
    }
  }
}
