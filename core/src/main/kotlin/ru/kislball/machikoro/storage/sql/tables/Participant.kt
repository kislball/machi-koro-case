package ru.kislball.machikoro.storage.sql.tables

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object Participant : Table("participants") {
  val paricipantId = integer("id").autoIncrement().uniqueIndex()
  val playerId = reference("player_id", Player.id)
  val gameId = reference("game_id", Game.id, onDelete = ReferenceOption.CASCADE)
  val cards = text("cards")
  val balance = integer("balance")
}
