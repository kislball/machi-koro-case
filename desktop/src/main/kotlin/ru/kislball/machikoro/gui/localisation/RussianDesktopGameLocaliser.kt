package ru.kislball.machikoro.gui.localisation

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
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.localisation.CompoundLocaliser
import ru.kislball.machikoro.localisation.Localiser
import ru.kislball.machikoro.localisation.MapLocaliser
import ru.kislball.machikoro.localisation.RussianLocaliser

private val DESKTOP_GAME_LOCALE_MAP: Map<String, (Any) -> String> =
    mapOf(
        "gui.app.title" to { "Machi Koro" },
        "gui.action.back" to { "Назад" },
        "gui.action.add_player" to { "Добавить игрока" },
        "gui.action.remove_player" to { "Удалить игрока" },
        "gui.management.leaderboard" to { "Таблица лидеров" },
        "gui.management.new_game_name" to { "Название новой игры" },
        "gui.management.create" to { "Создать" },
        "gui.management.or" to { "или" },
        "gui.management.create_dialog.title" to { obj: Any -> "Создание игры $obj" },
        "gui.management.create_dialog.player_name" to { "Имя игрока" },
        "gui.management.create_dialog.confirm" to { "OK" },
        "gui.top.title" to { "Топ игроков" },
        "gui.top.entry.name" to
            { obj: Any ->
              val (position, name) = obj as Pair<*, *>
              "$position. $name"
            },
        "gui.top.entry.wins" to { obj: Any -> "$obj" },
        "gui.game.finished.title" to { "Игра окончена" },
        "gui.game.finished.winner" to { obj: Any -> "Победитель: ${(obj as Player).name}" },
        "gui.game.player.balance" to
            { obj: Any ->
              val (name, balance) = obj as Pair<*, *>
              "$name ($balance)"
            },
        "gui.game.log.title" to { "События" },
        "gui.game.log.pending.roll_choice" to
            { obj: Any ->
              "Ожидается выбор количества кубиков от ${(obj as Player).name}"
            },
        "gui.game.log.pending.rethrow" to
            { obj: Any ->
              "Ожидается решение о перебросе от ${(obj as Player).name}"
            },
        "gui.game.log.pending.pick_player" to
            { obj: Any ->
              "Ожидается выбор игрока от ${(obj as Player).name}"
            },
        "gui.game.log.pending.swap" to
            { obj: Any ->
              "Ожидается выбор карт для обмена от ${(obj as Player).name}"
            },
        "gui.game.log.pending.additional_step" to
            { obj: Any ->
              "Ожидается решение о дополнительном ходе от ${(obj as Player).name}"
            },
        "gui.game.log.pending.buy_card" to
            { obj: Any ->
              "Ожидается выбор покупки от ${(obj as Player).name}"
            },
        "gui.game.prompt.swap.pick_opponent_card" to { "Выберите карту соперника для обмена" },
        "gui.game.prompt.swap.pick_own_card" to
            { obj: Any ->
              "Выберите свою карту для обмена с ${(obj as Player).name}"
            },
        "gui.game.prompt.buy.title" to { obj: Any -> "Покупка для $obj" },
        "gui.game.prompt.buy.description" to { "Выберите карту или пропустите покупку" },
        "gui.game.prompt.buy.price" to { obj: Any -> "Цена: $obj" },
        "gui.game.prompt.buy.remaining" to { obj: Any -> "Осталось: $obj" },
        "gui.game.prompt.buy.skip" to { "Пропустить" },
        "gui.game.prompt.pick_player" to { "Выберите игрока, с которого хотите взять деньги" },
        "gui.game.prompt.rolling" to { "Бросок..." },
        "gui.game.prompt.dice.title" to { obj: Any -> "$obj, бросьте кости" },
        "gui.game.prompt.dice.roll_one" to { "Бросить одну кость" },
        "gui.game.prompt.dice.roll_two" to { "Бросить две кости" },
        "gui.game.prompt.dice.roll" to { "Бросить кости" },
        "gui.game.prompt.rethrow.title" to { obj: Any -> "$obj, хотите перебросить кости?" },
        "gui.game.prompt.rethrow.confirm" to { "Перебросить" },
        "gui.game.prompt.rethrow.decline" to { "Оставить" },
        "gui.game.prompt.additional_step.title" to
            { obj: Any ->
              "$obj, выпал дубль. Вы можете походить ещё раз"
            },
        "gui.game.prompt.additional_step.confirm" to { "Хочу" },
        "gui.game.prompt.additional_step.decline" to { "Пропуск" },
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
        "gui.game.log.effect.effects.awaiter.cards.buy" to { "Ожидается выбор покупки" },
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

fun russianDesktopLocaliser(): Localiser =
    CompoundLocaliser(RussianDesktopGameLocaliser(), RussianLocaliser())

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
