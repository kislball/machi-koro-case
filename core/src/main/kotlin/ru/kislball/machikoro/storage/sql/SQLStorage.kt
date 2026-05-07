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
import org.jetbrains.exposed.v1.jdbc.update
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
    transaction { SchemaUtils.create(Game, Participant, Player) }
  }

  override fun save(name: String, game: GameDriver, catalogId: String) {
    transaction {
      val payload = payloadFor(game, catalogId)
      val createdAt = getCreatedAt(name)
      deleteInTransaction(name)

      val gameId =
          Game.insert {
                it[Game.catalog] = payload.metadata?.catalogId ?: catalogId
                createdAt?.let { savedCreatedAt -> it[Game.createdAt] = savedCreatedAt }
                it[Game.name] = name
                it[Game.finished] = payload.metadata?.finished ?: false
              }[Game.id]

      for (participant in payload.players) {
        val id =
            Participant.insert {
                  it[Participant.playerId] = getPlayerId(participant.name)
                  it[Participant.balance] = participant.balance
                  it[Participant.gameId] = gameId
                  it[Participant.cards] = participant.cards.joinToString(",")
                }[Participant.paricipantId]
        if (game.game.winner?.name == participant.name) {
          Game.update({ Game.id eq gameId }) { it[Game.winner] = id }
        }
      }
    }
  }

  override fun load(name: String): StoredGame {
    return transaction {
      val gameRow =
          Game.selectAll().where { Game.name eq name }.firstOrNull()
              ?: throw SaveNotFoundException(name)
      val gameId = gameRow[Game.id]
      val winnerName = gameRow[Game.winner]?.let { participantId -> getPlayerName(participantId) }
      val meta =
          GamePayloadMetadata(
              winnerName,
              gameRow[Game.catalog],
              finished = gameRow[Game.finished],
          )
      require(meta.catalogId != null)

      val participants =
          participantRows(gameId).map {
            PlayerPayload(
                it[Player.name], it[Participant.balance], parseCards(it[Participant.cards]))
          }
      val gamePayload =
          GamePayload(
              participants,
              meta,
          )
      toStoredGame(gamePayload)
    }
  }

  private fun participantRows(gameId: Int) =
      Participant.innerJoin(Player)
          .select(Player.name, Participant.balance, Participant.cards)
          .where { Participant.gameId eq gameId }

  private fun parseCards(cards: String): List<String> {
    return if (cards.isBlank()) emptyList() else cards.split(',')
  }

  private fun getPlayerName(participantId: Int): String? {
    return Participant.innerJoin(Player)
        .select(Player.name)
        .where { Participant.paricipantId eq participantId }
        .firstOrNull()
        ?.get(Player.name)
  }

  private fun getCreatedAt(name: String): OffsetDateTime? {
    return Game.select(Game.createdAt)
        .where { Game.name eq name }
        .firstOrNull()
        ?.get(Game.createdAt)
  }

  private fun getPlayerId(name: String): Int {
    return Player.select(Player.id).where { Player.name eq name }.firstOrNull()?.get(Player.id)
        ?: Player.insert { it[Player.name] = name }[Player.id]
  }

  override fun list(): List<SavedGameSummary> {
    return transaction {
      Game.selectAll().orderBy(Game.id to SortOrder.ASC).map { gameRow ->
        val gameId = gameRow[Game.id]
        SavedGameSummary(
            name = gameRow[Game.name],
            playerNames = participantRows(gameId).map { it[Player.name] },
            createdAt = gameRow[Game.createdAt].toInstant(),
            finished = gameRow[Game.finished],
            winnerName = gameRow[Game.winner]?.let { getPlayerName(it) },
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
