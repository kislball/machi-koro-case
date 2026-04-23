package ru.kislball.machikoro.cli.localiser

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cli.storage.TopEntry
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
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.localisation.MapLocaliser

private val CLI_LOCALE_MAP: Map<String, (Any) -> String> =
    mapOf(
        "cli.management.prompt" to { "management> " },
        "cli.game.prompt" to { obj: Any -> "game(${(obj as Player).name})> " },
        "cli.unknown_command" to { obj: Any -> "Неизвестная команда: $obj" },
        "cli.invalid_arguments" to { obj: Any -> "Неверные аргументы: $obj" },
        "cli.games.invalid_name" to { "Имя игры не должно быть пустым" },
        "cli.games.empty" to { "Нет сохранённых игр" },
        "cli.games.not_found" to { obj: Any -> "Игра не найдена: $obj" },
        "cli.catalog.not_found" to { obj: Any -> "Каталог не найден: $obj" },
        "cli.games.saved" to { obj: Any -> "Игра сохранена: $obj" },
        "cli.games.loaded" to { obj: Any -> "Игра загружена: $obj" },
        "cli.games.deleted" to { obj: Any -> "Игра удалена: $obj" },
        "cli.game.exited" to { "Выход в режим управления" },
        "cli.start.players_prompt" to { "Введите имена игроков через запятую" },
        "cli.start.players_invalid" to { "Нужно указать хотя бы одного игрока" },
        "cli.game.not_active" to { "Сейчас нет активной игры" },
        "cli.player.current_missing" to { "Текущий игрок не найден" },
        "cli.player.not_found" to { obj: Any -> "Игрок не найден: $obj" },
        "cli.swap.unexpected" to { "Сейчас обмен не ожидается" },
        "cli.swap.no_card" to { obj: Any -> "Карта не найдена: $obj" },
        "cli.swap.no_own_card" to { "Нет доступной карты для обмена" },
        "cli.dice.missing" to { "Сейчас нет броска, который можно изменить" },
        "cli.player.info" to
            { obj: Any ->
              val player = obj as Player
              val cards = player.cards.joinToString(", ") { cardName(it) }
              "Игрок ${player.name}: баланс ${player.balance}, карты: $cards"
            },
        "cli.cards.list" to
            { obj: Any ->
              val cards = obj as List<Card>
              cards.joinToString("\n") { card -> "${card.cardId} - ${cardName(card)}" }
            },
        "cli.top.entry" to { obj: Any -> "${(obj as TopEntry).playerName}: ${obj.wins}" },
        "cli.awaiting.rethrow" to
            { obj: Any ->
              "Ожидается решение о перебросе для ${(obj as Player).name}"
            },
        "cli.awaiting.pick_player" to
            { obj: Any ->
              "Ожидается выбор игрока для ${(obj as Player).name}"
            },
        "cli.awaiting.swap" to { obj: Any -> "Ожидается обмен карт для ${(obj as Player).name}" },
        "cli.awaiting.additional_step" to
            { obj: Any ->
              "Ожидается решение о дополнительном ходе для ${(obj as Player).name}"
            },
        "cli.effect" to { obj: Any -> effectMessage(obj as Effect) },
        "cli.effect.effects.utility.compound" to { "Составной эффект применён" },
        "cli.effect.effects.utility.noop" to { "Пустой эффект применён" },
        "cli.effect.effects.utility.maybe" to { "Опциональный эффект применён" },
        "cli.effect.effects.provide_input" to { "Ввод применён" },
        "cli.effect.effects.awaiter.money.pick_and_charge" to
            {
              "Ожидается выбор игрока для оплаты"
            },
        "cli.effect.effects.awaiter.cards.swap" to { "Ожидается выбор карт для обмена" },
        "cli.effect.effects.awaiter.dice.rethrow" to { "Ожидается решение о перебросе" },
        "cli.effect.effects.awaiter.order.additional_step" to
            {
              "Ожидается решение о дополнительном ходе"
            },
        "cli.effect.effects.dice.roll" to { "Кубики брошены" },
        "cli.effect.effects.sights.tv_tower.enable_rethrow" to { "Включена возможность переброса" },
        "cli.effect.effects.sights.railway_station.enable_two_dice" to
            {
              "Включена возможность бросать 2 кубика"
            },
        "cli.effect.effects.sights.shopping_centre.revenue_bonus" to
            {
              "Добавлен бонус к доходу магазинов и кафе"
            },
        "cli.effect.effects.order.additional_step.execute" to { "Назначен дополнительный ход" },
        "cli.effect.effects.game_finished" to { "Игра завершена" },
        "cli.dice.current" to
            { obj: Any ->
              val result = obj as DiceRollResult
              "Кости: ${result.diceThrown.joinToString(", ")}"
            },
        "cards.wheat.name" to { "Пшеница" },
        "cards.farm.name" to { "Ферма" },
        "cards.bakery.name" to { "Пекарня" },
        "cards.supermarket.name" to { "Супермаркет" },
        "cards.cafe.name" to { "Кафе" },
        "cards.family.name" to { "Семейный ресторан" },
        "cards.reserve.name" to { "Лес" },
        "cards.mine.name" to { "Шахта" },
        "cards.apples.name" to { "Яблоневый сад" },
        "cards.cheese_factory.name" to { "Сыроварня" },
        "cards.furniture.name" to { "Мебельная фабрика" },
        "cards.vegetables.name" to { "Овощной рынок" },
        "cards.stadium.name" to { "Стадион" },
        "cards.tv.name" to { "Телецентр" },
        "cards.business.name" to { "Бизнес-центр" },
        "cards.railway_station.name" to { "Вокзал" },
        "cards.shopping_centre.name" to { "Торговый центр" },
        "cards.tv_tower.name" to { "Телебашня" },
        "cards.entertainment_park.name" to { "Парк развлечений" },
    )

class RussianCLILocaliser : MapLocaliser(CLI_LOCALE_MAP)

@Suppress("UNCHECKED_CAST")
private fun cardName(card: Card): String {
  return CLI_LOCALE_MAP[card.cardNameKey]?.invoke(Unit) ?: card.cardId
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
  return CLI_LOCALE_MAP["cli.effect.$id"]?.invoke(Unit) ?: "Применён эффект"
}
