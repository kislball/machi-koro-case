package ru.kislball.machikoro.storage.sql

import java.time.OffsetDateTime
import org.jetbrains.exposed.v1.core.JoinType
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
      delete(name)

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
    val game =
        Game.join(
                Participant,
                JoinType.INNER,
                Game.winner,
                Participant.paricipantId,
            )
            .join(
                Player,
                JoinType.INNER,
                Participant.playerId,
                Player.id,
            )
            .selectAll()
            .firstOrNull() ?: throw SaveNotFoundException(name)
    val meta =
        GamePayloadMetadata(
            game[Player.name],
            game[Game.catalog],
            finished = game[Game.finished],
        )
    require(meta.catalogId != null)

    val participants =
        Participant.join(Player, JoinType.INNER, Player.id, Participant.playerId).selectAll().map {
          PlayerPayload(it[Player.name], it[Participant.balance], it[Participant.cards].split(','))
        }
    val gamePayload =
        GamePayload(
            participants,
            meta,
        )
    return toStoredGame(gamePayload)
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
    return Game.join(
            Participant,
            JoinType.LEFT,
            Game.winner,
            Participant.paricipantId,
        )
        .join(
            Player,
            JoinType.LEFT,
            Participant.playerId,
            Player.id,
        )
        .selectAll()
        .map { gameRow ->
          SavedGameSummary(
              name = gameRow[Game.name],
              playerNames =
                  Participant.join(Player, JoinType.INNER, Player.id, Participant.playerId)
                      .select(Player.name)
                      .where { Game.id eq gameRow[Game.id] }
                      .map { it[Game.name] },
              createdAt = gameRow[Game.createdAt].toInstant(),
              finished = gameRow[Game.finished],
              winnerName = gameRow[Player.name],
              catalogId = gameRow[Game.catalog],
          )
        }
        .toList()
  }

  override fun delete(name: String) {
    Game.deleteWhere { Game.name eq name }
  }
}
