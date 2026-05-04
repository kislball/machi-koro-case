package ru.kislball.machikoro.cards.common

import ru.kislball.machikoro.cards.standard.StandardCatalog

val CardCatalog.catalogId: String
  get() =
      when (this) {
        StandardCatalog -> "standard"
        else -> error("Unknown catalog id for ${this::class.qualifiedName}")
      }
