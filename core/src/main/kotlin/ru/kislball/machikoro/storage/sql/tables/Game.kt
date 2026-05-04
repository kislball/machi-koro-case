package ru.kislball.machikoro.storage.sql.tables

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone

object Game : Table("games") {
  val id = integer("id").autoIncrement().uniqueIndex()
  val name = text("name")
  val createdAt =
      timestampWithTimeZone("created_at").defaultExpression(CurrentTimestampWithTimeZone)
  val finished = bool("finished").default(false)
  val winner = reference("winner", Participant.paricipantId).nullable()
  val catalog = text("catalog")
}
