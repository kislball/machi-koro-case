package ru.kislball.machikoro.gui.game

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.effects.cards.GrantCardEffect
import ru.kislball.machikoro.effects.cards.RemoveCardEffect
import ru.kislball.machikoro.effects.input.AwaitInputEffect
import ru.kislball.machikoro.effects.input.ProvideInputEffect
import ru.kislball.machikoro.effects.money.FineEffect
import ru.kislball.machikoro.effects.money.MoneyTransferEffect
import ru.kislball.machikoro.effects.money.MoneyTransferType
import ru.kislball.machikoro.effects.order.GivePlayerAdditionalStepEffect
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.localisation.MapLocaliser

private val DESKTOP_GAME_LOCALE_MAP: Map<String, (Any) -> String> =
    mapOf(
        "gui.game.log.title" to { "События" },
        "gui.game.log.pending.roll_choice" to
            { obj: Any ->
              "Ожидается выбор количества кубиков от ${(obj as ru.kislball.machikoro.game.Player).name}"
            },
        "gui.game.log.pending.rethrow" to
            { obj: Any ->
              "Ожидается решение о перебросе от ${(obj as ru.kislball.machikoro.game.Player).name}"
            },
        "gui.game.log.pending.pick_player" to
            { obj: Any ->
              "Ожидается выбор игрока от ${(obj as ru.kislball.machikoro.game.Player).name}"
            },
        "gui.game.log.pending.swap" to
            { obj: Any ->
              "Ожидается выбор карт для обмена от ${(obj as ru.kislball.machikoro.game.Player).name}"
            },
        "gui.game.log.pending.additional_step" to
            { obj: Any ->
              "Ожидается решение о дополнительном ходе от ${(obj as ru.kislball.machikoro.game.Player).name}"
            },
        "gui.game.log.effect" to { obj: Any -> effectMessage(obj as Effect) },
        "gui.game.log.effect.effects.utility.compound" to { "Составной эффект применён" },
        "gui.game.log.effect.effects.utility.noop" to { "Пустой эффект применён" },
        "gui.game.log.effect.effects.utility.maybe" to { "Опциональный эффект применён" },
        "gui.game.log.effect.effects.provide_input" to { "Ввод применён" },
        "gui.game.log.effect.effects.awaiter.money.pick_and_charge" to
            {
              "Ожидается выбор игрока для оплаты"
            },
        "gui.game.log.effect.effects.awaiter.cards.swap" to { "Ожидается выбор карт для обмена" },
        "gui.game.log.effect.effects.awaiter.dice.rethrow" to { "Ожидается решение о перебросе" },
        "gui.game.log.effect.effects.awaiter.order.additional_step" to
            {
              "Ожидается решение о дополнительном ходе"
            },
        "gui.game.log.effect.effects.dice.roll" to { "Кубики брошены" },
        "gui.game.log.effect.effects.sights.tv_tower.enable_rethrow" to
            {
              "Включена возможность переброса"
            },
        "gui.game.log.effect.effects.sights.railway_station.enable_two_dice" to
            {
              "Включена возможность бросать 2 кубика"
            },
        "gui.game.log.effect.effects.sights.shopping_centre.revenue_bonus" to
            {
              "Добавлен бонус к доходу магазинов и кафе"
            },
        "gui.game.log.effect.effects.order.additional_step.execute" to
            {
              "Назначен дополнительный ход"
            },
        "gui.game.log.effect.effects.game_finished" to { "Игра завершена" },
        "gui.game.log.dice.current" to
            { obj: Any ->
              val result = obj as DiceRollResult
              "Кости: ${result.diceThrown.joinToString(", ")} (сумма ${result.diceThrown.sum()})"
            },
    )

class RussianDesktopGameLocaliser : MapLocaliser(DESKTOP_GAME_LOCALE_MAP)

@Suppress("UNCHECKED_CAST")
private fun cardName(card: Card): String {
  return DESKTOP_GAME_LOCALE_MAP[card.cardNameKey]?.invoke(Unit) ?: card.cardId
}

private fun effectMessage(effect: Effect): String {
  return when (effect) {
    is MoneyTransferEffect -> {
      val sign =
          when (effect.type) {
            MoneyTransferType.Deposit -> "+"
            MoneyTransferType.Withdraw,
            MoneyTransferType.WithdrawExact -> "-"
          }
      "Баланс ${effect.player.name}: $sign${effect.getChange()}"
    }
    is FineEffect -> "${effect.from.name} платит ${effect.to.name}: ${effect.getFinalAmount()}"
    is GrantCardEffect -> "${effect.player.name} получает карту ${cardName(effect.card)}"
    is RemoveCardEffect -> "${effect.from.name} теряет карту ${cardName(effect.card)}"
    is GivePlayerAdditionalStepEffect -> "${effect.player.name} получает дополнительный ход"
    is AwaitInputEffect<*> -> localiseEffectById(effect.id)
    is ProvideInputEffect<*> -> localiseEffectById(effect.id)
    else -> localiseEffectById(effect.id)
  }
}

private fun localiseEffectById(id: String): String {
  return DESKTOP_GAME_LOCALE_MAP["gui.game.log.effect.$id"]?.invoke(Unit) ?: "Применён эффект"
}
