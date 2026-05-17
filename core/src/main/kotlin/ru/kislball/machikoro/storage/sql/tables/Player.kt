package ru.kislball.machikoro.storage.sql.tables

import org.jetbrains.exposed.v1.core.Table

object Player : Table("players") {
  val name = text("name")

  override val primaryKey = PrimaryKey(name)
}
