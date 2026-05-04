# CLI

CLI работает в двух режимах:
- `management` для создания, загрузки и удаления сохранений
- `game` для команд активной игры

## Простая диаграмма классов

```mermaid
classDiagram
    class Main {
        +main()
    }

    class CLIApplication {
        -io: CLIIO
        -storage: GameStorage
        -session: CLISession
        -context: CommandContext
        +run()
        +execute(line: String)
    }

    class CLIIO {
        <<interface>>
        +readLine() String?
        +writeLine(text: String)
    }

    class GameStorage {
        <<abstract>>
        +save(name: String, game: GameDriver, catalogId: String)
        +load(name: String) StoredGame
        +list() List~SavedGameSummary~
        +delete(name: String)
        +top() List~TopEntry~
    }

    class StoredGame {
        +driver: GameDriver
        +catalogId: String
    }

    class SavedGameSummary {
        +name: String
        +playerNames: List~String~
        +createdAt: Instant
        +finished: Boolean
        +winnerName: String?
        +catalogId: String
    }

    class CLISession {
        +mode: CLIMode
        +activeGame: ActiveCliGame?
        +shouldExit: Boolean
    }

    class ActiveCliGame {
        +driver: GameDriver
        +catalogId: String
        +effectLog: MutableList~String~
    }

    class CommandContext {
        +io: CLIIO
        +storage: GameStorage
        +session: CLISession
        +autoAdvance: GameAutoAdvance
        +catalogs: CLICatalogRegistry
    }

    class Command {
        <<abstract>>
        +name: String
        +mode: CLIMode
        +execute(arguments: List~String~, context: CommandContext)
    }

    class GameAutoAdvance {
        +advance(game: ActiveCliGame)
    }

    class CLICatalogRegistry {
        +defaultCatalogId: String
        +get(id: String) CLICatalogDefinition?
        +require(id: String) CLICatalogDefinition
    }

    class GameStorageFactory {
        <<object>>
        +json(root: String, defaultCatalogId: String, catalogResolver: CardCatalogResolver) GameStorage
    }

    class JsonGameStorage
    class GameDriver

    Main ..> CLIApplication
    CLIApplication ..> CLIIO
    CLIApplication ..> GameStorage
    CLIApplication ..> GameStorageFactory : default JSON storage
    CLIApplication ..> CLISession
    CLIApplication ..> CommandContext
    CLIApplication ..> Command
    CommandContext ..> CLIIO
    CommandContext ..> GameStorage
    CommandContext ..> CLISession
    CommandContext ..> GameAutoAdvance
    CommandContext ..> CLICatalogRegistry
    CLISession ..> ActiveCliGame
    ActiveCliGame ..> GameDriver
    JsonGameStorage --|> GameStorage
    GameStorageFactory ..> JsonGameStorage
    GameStorage ..> StoredGame
    GameStorage ..> SavedGameSummary
    StoredGame ..> GameDriver
```

## Базовая архитектура

### Точка входа
- `MainKt.main()` создаёт `CLIApplication` и запускает `run()`.
- `CLIApplication` читает команды, печатает prompt и запускает команду для текущего `CLIMode`.

### Состояние сессии
- `CLISession` хранит текущий режим и ссылку на `ActiveCliGame`.
- `ActiveCliGame` хранит `GameDriver`, `catalogId` и локальный `effectLog`. CLI печатает этот лог после команды.

### Выполнение команд
- `CommandContext` хранит зависимости CLI в одном месте: I/O, storage, session, auto-advance, localiser и registry каталогов.
- Команды разделены на два набора:
  - `managementCommands()` для `start`, `load`, `list`, `delete`, `top`, `exit`
  - `gameCommands()` для `save`, `info`, `buyCard`, `roll`, `pickPlayer`, `swap`, `rethrow`, `keep`, `takeAdditionalStep`, `skipAdditionalStep`, `listCards`, `addTwo`
- После каждой команды `CLIApplication` вызывает `flushGameState()`: продвигает игру дальше, печатает effect log и показывает ожидаемый input effect, если он есть.

### Каталоги и сохранения
- Сохранения доступны CLI через абстрактный `GameStorage`.
- По умолчанию `CLIApplication` создаёт JSON-хранилище через `GameStorageFactory.json(root: String, ...)`.
- CLI использует только `GameStorage` и превращает `StoredGame` в `ActiveCliGame`.
- `CLICatalogRegistry` хранит доступные каталоги и каталог по умолчанию.
- При `start` CLI создаёт игру на каталоге по умолчанию.
- При `save` в JSON пишутся `catalogId` и `finished`.
- При `load` JSON-хранилище читает `catalogId` из файла и загружает игру с нужным `CardCatalog`.
- `GameStorage.list()` возвращает не только имя, но и краткую информацию: игроков, `createdAt`, `finished`, `winnerName` и `catalogId`.
- Если в старом сохранении нет поля `finished`, оно вычисляется по `winner`.

## Режим игры
1. `save <name>` --- сохранить игру под данным названием
2. `info [playername]` --- информация об игроке, если не указан, то выводится информация о текущем игроке
3. `buyCard <cardid>` --- купить карточку, если у этого игрока есть такая возможность
4. `roll <1|2>` --- бросить 1 или 2 кубика, если игра ожидает выбор количества кубиков
5. `pickPlayer <playername>` --- выбрать игрока, с которого необходимо взять оплату
6. `swap <playername> <theirCardId> <yourCardId>` --- обменяться выбранными карточками с игроком
7. `rethrow` --- перебросить кубики, если у игрока есть такая возможность
8. `keep` --- оставить текущий бросок без переброса
9. `takeAdditionalStep` --- согласиться на дополнительный ход
10. `skipAdditionalStep` --- отказаться от дополнительного хода
11. `listCards` --- выводит список карточек
12. `addTwo` --- добавить 2 к числам на кубике

## Режим управления
1. `top` --- топ игроков по выйгрышам
2. `list` --- список игр
3. `load <name>` --- загрузить игру
4. `delete <name>` --- удалить игру
5. `exit` --- выйти из игры
6. `start` --- создание новой игры
