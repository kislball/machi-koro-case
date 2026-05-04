package ru.kislball.machikoro.storage.sql.tables

import org.jetbrains.exposed.v1.core.Table

object Player : Table("players") {
  val id = integer("id").autoIncrement().uniqueIndex()
  val name = text("name")
}
