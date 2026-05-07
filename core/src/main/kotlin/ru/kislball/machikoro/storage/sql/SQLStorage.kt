package ru.kislball.machikoro.storage.sql

import java.time.OffsetDateTime
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.kislball.machikoro.cards.common.CardCatalogResolver
import ru.kislball.machikoro.exceptions.SaveNotFoundException
import ru.kislball.machikoro.facility.GameDriver
import ru.kislball.machikoro.facility.payload.GamePayload
import ru.kislball.machikoro.facility.payload.GamePayloadMetadata
import ru.kislball.machikoro.facility.payload.PlayerPayload
import ru.kislball.machikoro.storage.GameStorage
import ru.kislball.machikoro.storage.SavedGameSummary
import ru.kislball.machikoro.storage.StoredGame
import ru.kislball.machikoro.storage.sql.tables.Game
import ru.kislball.machikoro.storage.sql.tables.Participant
import ru.kislball.machikoro.storage.sql.tables.Player

class SQLStorage(
    defaultCatalogId: String,
    catalogResolver: CardCatalogResolver,
) : GameStorage(defaultCatalogId, catalogResolver) {
  init {
    transaction { SchemaUtils.create(Player, Game, Participant) }
  }

  override fun save(name: String, game: GameDriver, catalogId: String) {
    transaction {
      val payload = payloadFor(game, catalogId)
      val createdAt = getCreatedAt(name)
      deleteInTransaction(name)
      payload.players.forEach { ensurePlayer(it.name) }

      Game.insert {
        it[Game.catalog] = payload.metadata?.catalogId ?: catalogId
        createdAt?.let { savedCreatedAt -> it[Game.createdAt] = savedCreatedAt }
        it[Game.name] = name
        it[Game.finished] = payload.metadata?.finished ?: false
        it[Game.winner] = game.game.winner?.name
      }

      for (participant in payload.players) {
        Participant.insert {
          it[Participant.playerName] = participant.name
          it[Participant.balance] = participant.balance
          it[Participant.gameName] = name
          it[Participant.cards] = participant.cards.joinToString(",")
        }
      }
    }
  }

  override fun load(name: String): StoredGame {
    return transaction {
      val gameRow =
          Game.selectAll().where { Game.name eq name }.firstOrNull()
              ?: throw SaveNotFoundException(name)
      val meta =
          GamePayloadMetadata(
              gameRow[Game.winner],
              gameRow[Game.catalog],
              finished = gameRow[Game.finished],
          )
      require(meta.catalogId != null)

      val participants =
          participantRows(name).map {
            PlayerPayload(
                it[Participant.playerName],
                it[Participant.balance],
                parseCards(it[Participant.cards]),
            )
          }
      val gamePayload =
          GamePayload(
              participants,
              meta,
          )
      toStoredGame(gamePayload)
    }
  }

  private fun participantRows(gameName: String) =
      Participant.select(Participant.playerName, Participant.balance, Participant.cards).where {
        Participant.gameName eq gameName
      }

  private fun parseCards(cards: String): List<String> {
    return if (cards.isBlank()) emptyList() else cards.split(',')
  }

  private fun getCreatedAt(name: String): OffsetDateTime? {
    return Game.select(Game.createdAt)
        .where { Game.name eq name }
        .firstOrNull()
        ?.get(Game.createdAt)
  }

  private fun ensurePlayer(name: String) {
    if (Player.select(Player.name).where { Player.name eq name }.empty()) {
      Player.insert { it[Player.name] = name }
    }
  }

  override fun list(): List<SavedGameSummary> {
    return transaction {
      Game.selectAll().orderBy(Game.name to SortOrder.ASC).map { gameRow ->
        val name = gameRow[Game.name]
        SavedGameSummary(
            name = name,
            playerNames = participantRows(name).map { it[Participant.playerName] },
            createdAt = gameRow[Game.createdAt].toInstant(),
            finished = gameRow[Game.finished],
            winnerName = gameRow[Game.winner],
            catalogId = gameRow[Game.catalog],
        )
      }
    }
  }

  override fun delete(name: String) {
    transaction { deleteInTransaction(name) }
  }

  private fun deleteInTransaction(name: String) {
    Game.deleteWhere { Game.name eq name }
  }
}
