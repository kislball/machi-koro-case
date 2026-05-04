package ru.kislball.machikoro.cards.common

import ru.kislball.machikoro.cards.standard.StandardCatalog

data class CardCatalogDefinition(
    val id: String,
    val catalog: CardCatalog,
)

class CardCatalogResolver(definitions: List<CardCatalogDefinition>) {
  constructor(vararg definitions: CardCatalogDefinition) : this(definitions.toList())

  private val catalogsById = definitions.associate { it.id to it.catalog }

  init {
    require(catalogsById.size == definitions.size) { "Catalog ids must be unique" }
  }

  operator fun get(id: String): CardCatalog? = catalogsById[id]

  fun getCatalogList(): List<CardCatalog> = catalogsById.values.toList()

  companion object {
    val default =
        CardCatalogResolver(CardCatalogDefinition(StandardCatalog.catalogId, StandardCatalog))
  }
}
