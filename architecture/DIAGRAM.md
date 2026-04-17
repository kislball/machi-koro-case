```mermaid
classDiagram
    namespace Triggers {
        class Trigger {
            <<abstract>>
            +triggerDescriptionKey: String
            +triggerNameKey: String
            +isTriggered(step: Step) Boolean
        }
        class AnyDiceTrigger {
            +dice: List~Int~
        }
        class PlayerDiceTrigger {
            +player: Player
            +dice: List~Int~
        }
        class AndTrigger {
            +triggers: List~Trigger~
        }
        class OrTrigger {
            +triggers: List~Trigger~
        }
    }

    namespace Effects {
        class Effect {
            <<abstract>>
            +effectDescriptionKey: String
            +effectNameKey: String
            +apply(step: Step) void
        }
        class CompoundEffect {
            +effects: List~Effect~
            +combineEffects(effects: Effect...) CompoundEffect
        }
        class MoneyTransferEffect {
            +from: Player?
            +to: Player?
            +amount: Int
        }
        class GrantCardEffect {
            +player: Player
            +kind: CardKind
        }
    }

    namespace Actions {
        class PlayerAction {
            <<abstract>>
            +player: Player
            +checkValid(step: Step) void
            +getEffect(step: Step) Effect
        }
        class BuyCardAction {
            +kind: CardKind
        }
    }

    namespace Cards {
        class Triggerable {
            <<abstract>>
            +getEffect(step: Step) Effect
            +apply(step: Step) void
        }
        class Card {
            <<abstract>>
            +cardNameKey: String
            +cardNameDescription: String
            +kind: CardKind
            +getPrice(step: Step) Int
        }
        class CardFactory {
            <<object>>
            +canCreate(kind: CardKind) Boolean
            +create(kind: CardKind) Card
        }
        class CardCatalog {
            <<object>>
            +allKinds() List~CardKind~
            +kindsByType(type: CardType) List~CardKind~
            +contains(kind: CardKind) Boolean
            +getCreator(kind: CardKind) Function
        }
        class CardKind {
            <<enum>>
        }
        class CardType {
            <<enum>>
        }
    }

    namespace CoreGame {
        class Step {
            +game: Game
            +currentPlayer: Player
            +stepNumber: Int
        }
        class WaitingDiceStep {
            +rollDice(numDice: Int) DiceRolledStep
        }
        class DiceRolledStep {
            +dice: List~Int~
            +finish(action: PlayerAction) FinishedActionStep
        }
        class FinishedActionStep {
            +action: PlayerAction
            +effect: Effect
        }
        class Game {
            +players: List~Player~
            +steps: MutableList~Step~
            +currentStep: Step?
            +currentPlayer: Player?
            +nextStep() Step
            +getTriggerables() Sequence~Triggerable~
            +countCardsOfKind(kind: CardKind) Int
        }
        class Player {
            +name: String
            +balance: Int
            +cards: MutableList~Card~
        }
    }

    namespace GameFacility {
        class GameExporter {
            <<interface>>
            +export(game: Game) String
        }
        class GameImporter {
            <<interface>>
            +import(content: String) Game
        }
        class JSONExporter
        class JSONImporter
        class GameDriver {
            +game: Game
            +nextStep() WaitingDiceStep
            +rollDice(player: Player, numDice: Int) DiceRolledStep
            +finishStep(action: PlayerAction) FinishedActionStep
        }
        class GameFactory {
            <<object>>
            +createDriver(playerNames: List~String~) GameDriver
            +export(game: Game, exporter: GameExporter) String
            +import(content: String, importer: GameImporter) GameDriver
        }
    }

    Trigger <|-- AnyDiceTrigger
    Trigger <|-- PlayerDiceTrigger
    Trigger <|-- AndTrigger
    Trigger <|-- OrTrigger
    Trigger <|-- Triggerable
    Triggerable <|-- Card

    Effect <|-- CompoundEffect
    Effect <|-- MoneyTransferEffect
    Effect <|-- GrantCardEffect

    PlayerAction <|-- BuyCardAction
    Step <|-- WaitingDiceStep
    Step <|-- DiceRolledStep
    Step <|-- FinishedActionStep

    CardKind --> CardType : type
    Card --> CardKind : kind
    Player "1" o-- "*" Card : owns
    Game "1" o-- "*" Player : contains
    Game "1" o-- "*" Step : history

    PlayerDiceTrigger --> Player
    MoneyTransferEffect --> Player
    GrantCardEffect --> Player
    GrantCardEffect --> CardKind
    GrantCardEffect ..> CardFactory : creates
    CardFactory ..> CardCatalog : uses
    CardFactory ..> Card : creates

    Trigger ..> Step
    Effect ..> Step
    PlayerAction ..> Effect : returns
    Game ..> Triggerable : getTriggerables
    DiceRolledStep ..> PlayerAction : finish
    FinishedActionStep --> PlayerAction
    FinishedActionStep --> Effect

    JSONExporter ..|> GameExporter
    JSONImporter ..|> GameImporter
    GameDriver --> Game
    GameFactory ..> Game : creates
    GameFactory ..> GameDriver : creates
    GameFactory ..> GameExporter : uses
    GameFactory ..> GameImporter : uses
```

Для описания дальнейшей логики используется диаграма классов выше. Далее будут описаны отдельные моменты.

#### Роль GameDriver
Основная роль --- ведение игры. В неё входит:
1. Приём команд от разных игроков, проверка соответствует ли порядок ходов правилам
2. Обработка ошибок на каждую команду, возвращение результата для игрока
3. Правильное завершение игры

### Эффекты
Эффекты --- единственное, что может изменять игру. Действия игроков и карточки порождают эффекты,
которые в свою очередь ответственны за поддержку инвариантов.























