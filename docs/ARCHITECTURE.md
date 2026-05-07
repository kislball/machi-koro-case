## Основные модули

### Модель игры
- `Game`: владеет игроками, историей шагов и очередью отложенных input-эффектов.
- `Player`: хранит игрока, баланс и принадлежащие ему карты.
- `StepPhase`, `PendingStepPhase`, `FinishedStepPhase` описывают шаг игры. Шаг заканчивается, когда больше нет эффектов, которые ждут ввод пользователя.
- `DiceRollResult`: результат броска кубиков для игрока.
- `ClassMap`: структура для безопасного хранения разных типов данных.
- `InputEffectsQueue`: очередь эффектов, которые ждут ввод пользователя.

### Карты, триггеры и эффекты
Это слой, где живут правила.

#### Карты (`Cards`)
- `Card` — базовый интерфейс для всех типов карт.
- Карта возвращает эффект через `getEffect(stepPhase, possessor)`.
- Конкретные карты, например `NatureCard` или `BusinessCentreCard`, задают свою логику.
- `CardCatalog` предоставляет поиск карты по id.

#### Триггеры (`Triggers`)
Триггеры решают, должно ли сработать событие.
- `Trigger` — предикат: `isTriggered(stepPhase, possessor)`.
- Композиция:
  - `AndTrigger` / `OrTrigger` объединяют триггеры.
  - Кубиковые триггеры (`AnyDiceTrigger`, `PlayerDiceTrigger`, `PossessorDiceTrigger`) проверяют результат броска.

#### Эффекты (`Effects`)
Эффекты — единственный компонент, который может менять состояние игры.
- `Effect.apply(stepPhase)` — применяет эффект.
- `CompoundEffect` собирает несколько эффектов в один.
- `NoopEffect` ничего не делает.
- `MaybeEffect` применяет эффект и игнорирует ошибки внутри него.
- Конкретные эффекты:
  - `MoneyTransferEffect` (с `MoneyTransferType`)
  - `FineEffect`
  - `GrantCardEffect` / `RemoveCardEffect`

### Слой ввода игрока (`Effects`, `GameFacility`)
Некоторые карты и действия требуют дополнительного выбора игрока.

#### Очередь инпут эффектов
- `AwaitInputEffect` кладёт `InputEffect<T>` в `Game.inputEffects`.
- Пока `Game.inputEffects` не пустая, текущий шаг нельзя завершить.
- `ProvideInputEffect` применяет `InputEffect<T>` только после:
  - проверки ввода без контекста шага (`checkInput`)
  - проверки ввода с учётом шага (`isValid(stepPhase, input)`)
  - применения эффекта через `applyWithInput`.

## Поток выполнения

### Продвижение по шагам
Логика такая:
1. `GameDriver` создаёт новый `StepPhase`.
2. `StepPhase.runTriggerables()` получает доступные `triggerable` через `Game.getTriggerables()` и проверяет их.
3. Сработавшие `triggerable` создают эффекты (`Triggerable.getEffect`).
4. Эффекты применяются в контексте текущего шага.
5. Если нужен ввод, эффекты кладут `input effects` в очередь, и шаг не завершается.
6. Когда ожидаемого ввода больше нет, шаг переходит в finished phase и может быть закрыт.

### Роль `GameDriver`
`GameDriver` — оркестратор:
- принимает команды
- обеспечивает корректность хода/фазы
- продвигает фазу игры
- реализует `finishStep`, который может вернуть `null`, если `Game.inputEffects` не пуста
- даёт простой интерфейс для работы с эффектами

## Расширение игры

При добавлении новых карт:
1. Опишите/реализуйте эффект(ы) карты в `cards`.
2. Если карте нужен выбор игрока, добавьте новый `InputEffect<T>`:
   - реализуйте `checkInput`, `isValid`, `applyWithInput`
   - обеспечьте путь через `AwaitInputEffect`/`ProvideInputEffect` в композиции эффектов карты
3. Если карта должна реагировать на условия (кубики/ход/фаза), задайте нужную композицию `Trigger` или добавьте новый тип триггера.

При добавлении нового способа хранения:
- реализуйте новый наследник `GameStorage`
- добавьте значение в `StorageBackend`, если способ хранения должен выбираться из CLI/GUI
- создайте его через `GameStorageFactory`
- используйте `GamePayload` 
- переопределите `top()`, если хранилище может посчитать таблицу побед эффективнее, чем через `list()`

## Сохранения

- `GameStorage` — абстракция хранилища сохранений. Она работает с `GameDriver`, `StoredGame`, `SavedGameSummary` и `TopEntry`.
- CLI, desktop и другие интерфейсы зависят только от `GameStorage`, а не от конкретного JSON-хранилища.
- `StorageBackend` описывает выбираемые пользователем backend'ы: `JSON` и `SQL`.
- `GameStorageFactory.create(backend, root, ...)` создаёт хранилище по выбранному backend'у.
- `GameStorageFactory.json(root: String, ...)` создаёт стандартное файловое JSON-хранилище.
- `GameStorageFactory.h2(databasePath: String, ...)` подключает H2 database и создаёт SQL-хранилище.
- `JsonGameStorage` — внутренняя файловая JSON-реализация.
- `SQLStorage` — реализация на Exposed/JDBC. Она хранит игры, игроков и участников в таблицах `games`, `players`, `participants`.
- В SQL-схеме имя сохранения (`games.name`) и имя игрока (`players.name`) являются primary key. Участник игры задаётся парой `participants.game_name + participants.player_name`.
- `JSONImporter` и `JSONExporter` — конкретные JSON-сериализаторы
- JSON-метаданные сохранения сейчас включают:
  - `catalogId` для восстановления нужного каталога карт
  - `winner` для таблицы побед и совместимости со старыми сохранениями
  - `finished` как явный флаг завершённой игры
- Дата создания не хранится в JSON. `JsonGameStorage.list()` берёт её из метаданных файла (`creationTime`).
- Если в старом файле нет поля `finished`, используется правило `winner != null`.
- SQL-хранилище хранит `createdAt`, `finished`, `winner` и `catalog` в таблице `games`.
- Победитель в SQL хранится как имя игрока (`games.winner -> players.name`).
- При использовании `GameStorageFactory.create(StorageBackend.SQL, root, ...)` H2-файл создаётся по пути `${root}/machikoro.mv.db`.
- JSON и SQL backend'ы не мигрируют данные друг в друга автоматически. При переключении backend'а интерфейс показывает сохранения выбранного хранилища.

## Выбор хранилища в интерфейсах

- CLI стартует с JSON-хранилищем и может переключиться командой `storage sql` или `storage json` в режиме управления.
- `CommandContext` хранит текущий `GameStorage`, выбранный `StorageBackend` и фабрику, через которую команда `storage` пересоздаёт хранилище.
- Desktop GUI хранит выбранный backend в `AppUiState.storageBackend`.
- На экране выбора игры GUI показывает кнопку текущего хранилища. Нажатие переключает `JSON <-> SQL`, возвращает приложение на экран выбора игры и обновляет список сохранений из нового backend'а.
- Подписи GUI для хранилища локализуются ключами `gui.storage.current`, `gui.storage.json`, `gui.storage.sql`.
