package ru.kislball.machikoro.localisation

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Player

private val RUSSIAN_LOCALE_MAP: Map<String, (Any) -> String> =
    mapOf(
        "exception.player.not_found" to { obj: Any -> "Игрок ${(obj as Player).name} не найден" },
        "exception.insufficient_funds" to
            { obj: Any ->
              "Недостаточно средств у игрока ${(obj as Player).name}"
            },
        "exception.dice.not_rolled" to { obj: Any -> "Кости ещё не брошены" },
        "exception.game.finished" to { obj: Any -> "Игра уже завершена" },
        "exception.player.not_current" to
            { obj: Any ->
              "Только игрок ${(obj as Player).name} может выполнить это действие"
            },
        "exception.dice.invalid_count" to
            { obj: Any ->
              "Игрок ${(obj as Player).name} не может бросить столько костей"
            },
        "exception.dice.invalid_input" to
            { obj: Any ->
              "Неверный ввод броска костей: ${(obj as Int)}"
            },
        "exception.dice.already_rolled" to { obj: Any -> "Кости уже брошены" },
        "exception.rethrow.pending" to { obj: Any -> "Ожидается решение о перебросе" },
        "exception.input.pending" to { obj: Any -> "Ожидается ввод" },
        "exception.card.not_found" to { obj: Any -> "Карта ${obj} не найдена" },
        "exception.card.not_enough" to { obj: Any -> "Карты типа $obj больше не доступны" },
        "exception.card.cannot_purchase" to
            { obj: Any ->
              val parts = obj as? List<Any> ?: listOf(obj)
              "Игрок ${parts.getOrNull(0)} не может купить карту ${parts.getOrNull(1) ?: obj}"
            },
        "exception.step.not_finishable" to { obj: Any -> "Шаг не может быть завершён" },
        "exception.player.card_missing" to
            { obj: Any ->
              val parts = obj as? List<Any> ?: listOf(obj)
              "Игрок ${parts.getOrNull(0)} не имеет карту ${parts.getOrNull(1) ?: obj}"
            },
        "exception.swap.self" to { obj: Any -> "Игрок не может обменяться картами с собой" },
        "exception.swap.sights" to { obj: Any -> "Достопримечательности нельзя обменивать" },
        "exception.swap.special" to { obj: Any -> "Специальные карты нельзя обменивать" },
        "exception.player.already_added" to
            { obj: Any ->
              "Игрок ${(obj as Player).name} уже добавлен"
            },
        "exception.player.name_blank" to { obj: Any -> "Имя игрока не должно быть пустым" },
        "exception.player.names_not_unique" to
            { obj: Any ->
              "Имена игроков должны быть уникальными"
            },
        "exception.card.duplicate" to { obj: Any -> "Найдены дубликаты карт" },
        "exception.player_list.empty" to { obj: Any -> "Список игроков не должен быть пустым" },
        "exception.effect.invalid" to { obj: Any -> "Эффект $obj невалиден" },
        "exception.effect.input_invalid" to
            { obj: Any ->
              val parts = obj as? List<Any> ?: listOf(obj)
              "Неверный ввод для эффекта ${parts.getOrNull(0)}: ${parts.getOrNull(1) ?: obj}"
            },
        "exception.effect.input_type_mismatch" to
            { obj: Any ->
              "Ввод должен быть игроком для эффекта $obj"
            },
        "exception.step.substitution_invalid" to
            { obj: Any ->
              "Подстановка невозможна - этот шаг не может быть завершён"
            },
        "exception.step.not_ready" to { obj: Any -> "Текущий шаг не готов для действия игрока" },
        "exception.step.type_mismatch" to
            { obj: Any ->
              val parts = obj as? List<Any> ?: listOf(obj)
              "Ожидался ${parts.getOrNull(0)}, получен ${parts.getOrNull(1) ?: obj}"
            },
        "exception.effect.not_awaiting_input" to { obj: Any -> "Эффект $obj не ожидает ввода" },
        "exception.input.non_owner" to { obj: Any -> "Ввод предоставлен не владельцем" },
        "exception.input.effect_mismatch" to { obj: Any -> "Текущий шаг не ожидает $obj" },
        "exception.possessor.not_set" to { obj: Any -> "Владелец должен быть установлен" },
        "exception.no_filler_cards" to
            { obj: Any ->
              "Нет доступных заполняющих карт для завершения шага"
            },
        "exception.localisation.key_not_found" to
            { obj: Any ->
              "Ключ локализации не найден: $obj"
            },
        "exception.localisation.input_invalid" to
            { obj: Any ->
              "Неверный ввод для ключа локализации: $obj"
            },
        "player.joined" to { obj: Any -> "Игрок ${(obj as Player).name} присоединился" },
        "player.left" to { obj: Any -> "Игрок ${(obj as Player).name} покинул игру" },
        "dice.rolled" to { obj: Any -> "Брошены кости: ${(obj as DiceRollResult).diceThrown}" },
        "dice.rolled_sum" to { obj: Any -> "Сумма: ${(obj as DiceRollResult).diceThrown.sum()}" },
        "game.started" to { obj: Any -> "Игра началась" },
        "game.finished" to { obj: Any -> "Игра завершена" },
        "step.started" to { obj: Any -> "Ход ${(obj as Int)} начался" },
        "step.finished" to { obj: Any -> "Ход ${(obj as Int)} завершён" },
        "card.bought" to
            { obj: Any ->
              "Игрок ${(obj as? List<Any>)?.getOrNull(0)} купил карту ${(obj as? List<Any>)?.getOrNull(1) ?: obj}"
            },
        "card.activated" to { obj: Any -> "Карта ${(obj as Card).cardId} активирована" },
        "effect.applied" to { obj: Any -> "Эффект $obj применён" },
        "balance.changed" to
            { obj: Any ->
              val parts = obj as? List<Any> ?: listOf(obj)
              "Баланс игрока ${parts.getOrNull(0)} изменён на ${parts.getOrNull(1)}"
            },
        "turn.current" to { obj: Any -> "Ход игрока ${(obj as Player).name}" },
        "cards.cafe.name" to { "Кафе" },
        "cards.family.name" to { "Семейный ресторан" },
        "cards.wheat.name" to { "Пшеничное поле" },
        "cards.farm.name" to { "Ферма" },
        "cards.reserve.name" to { "Лес" },
        "cards.mine.name" to { "Шахта" },
        "cards.apples.name" to { "Яблоневый сад" },
        "cards.bakery.name" to { "Пекарня" },
        "cards.supermarket.name" to { "Супермаркет" },
        "cards.cheese_factory.name" to { "Сыроварня" },
        "cards.furniture.name" to { "Мебельная фабрика" },
        "cards.vegetables.name" to { "Овощебаза" },
        "cards.stadium.name" to { "Стадион" },
        "cards.tv.name" to { "Телецентр" },
        "cards.business.name" to { "Бизнес-центр" },
        "cards.railway_station.name" to { "Вокзал" },
        "cards.shopping_centre.name" to { "Торговый центр" },
        "cards.tv_tower.name" to { "Радиовышка" },
        "cards.entertainment_park.name" to { "Парк развлечений" })

class RussianLocaliser : MapLocaliser(RUSSIAN_LOCALE_MAP)
