package ru.kislball.machikoro.storage.sql.tables

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object Participant : Table("participants") {
  val gameName = reference("game_name", Game.name, onDelete = ReferenceOption.CASCADE)
  val playerName = reference("player_name", Player.name)
  val cards = text("cards")
  val balance = integer("balance")

  override val primaryKey = PrimaryKey(gameName, playerName)
}
