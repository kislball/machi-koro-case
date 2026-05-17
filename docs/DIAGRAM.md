```mermaid
classDiagram
    namespace Triggers {
        class Trigger {
            <<abstract>>
            +id: String
            +triggerNameKey: String
            +triggerDescriptionKey: String
            +isTriggered(stepPhase: StepPhase, possessor: Player?) Boolean
        }
        class Triggerable {
            <<abstract>>
            +triggerableId: String
            +getEffect(stepPhase: StepPhase, possessor: Player?) Effect
            +apply(stepPhase: StepPhase, possessor: Player?) void
        }
        class AnyDiceTrigger
        class PlayerDiceTrigger {
            +player: Player
        }
        class PossessorDiceTrigger
        class BooleanTrigger
        class AndTrigger {
            +triggers: List~Trigger~
        }
        class OrTrigger {
            +triggers: List~Trigger~
        }
        class SightsCollectedTrigger
    }

    namespace Effects {
        class Effect {
            <<abstract>>
            +id: String
            +effectNameKey: String
            +effectDescriptionKey: String
            +isValid(stepPhase: StepPhase) Boolean
            +run(stepPhase: StepPhase) void
            +apply(stepPhase: StepPhase) void
        }
        class CompoundEffect {
            +effects: List~Effect~
            +combineEffects(effects: Effect...) CompoundEffect
        }
        class NoopEffect
        class MaybeEffect {
            +inner: Effect
        }
        class MoneyTransferEffect {
            +player: Player
            +amount: Int
            +type: MoneyTransferType
        }
        class MoneyTransferType {
            <<enum>>
        }
        class FineEffect {
            +from: Player
            +to: Player
            +amount: Int
        }
        class GrantCardEffect {
            +player: Player
            +card: Card
        }
        class RemoveCardEffect {
            +from: Player
            +card: Card
        }
        class GivePlayerAdditionalStepEffect {
            +player: Player
        }
        class InputEffect~T~ {
            <<abstract>>
            +id: String
            +player: Player
            +checkInput(input: T) Boolean
            +isValid(stepPhase: StepPhase, input: T) Boolean
            +applyWithInput(stepPhase: StepPhase, input: T) void
            +getEffect(input: T) Effect
        }
        class AwaitInputEffect~T~ {
            +player: Player
        }
        class ProvideInputEffect~T~ {
            +effect: InputEffect~T~
            +input: T
            +fromPlayer: Player
        }
        class DiceRollInputEffect
        class RethrowDiceInputEffect
        class BuyCardInputEffect
        class BuyCardDecisionResolved
        class PickAndChargeUserInputEffect {
            +to: Player
            +amount: Int
        }
        class GivePlayerAdditionalStepInputEffect
        class SwapCardsInputEffect {
            +to: Player
            +getAwaiter(to: Player) AwaitInputEffect
        }
        class SwapCardsInput {
            +from: Player
            +fromCard: Card
            +toCard: Card
        }
    }

    namespace Actions {
        class PlayerAction {
            <<abstract>>
            +id: String
            +player: Player
            +actionNameKey: String
            +actionDescriptionKey: String
            +checkValid(stepPhase: StepPhase) void
            +getEffect(stepPhase: StepPhase) Effect
        }
        class BuyCardAction {
            +card: Card
        }
        class BuyCardInputAction
        class ProvideRethrowDecisionAction
        class PickAndChargePlayerAction
        class SwapCardsAction
        class ProvideAdditionalStepDecisionAction
    }

    namespace Cards {
        class Card {
            <<abstract>>
            +cardId: String
            +type: CardType
            +totalCards: Int
            +icon: CardIcon
            +cardNameKey: String
            +cardDescriptionKey: String
            +getPrice(stepPhase: StepPhase) Int
        }
        class CardCatalog {
            +get(id: String) Card?
            +getCardList() List~Card~
            +getStarterCards() List~Card~
        }
        class CompoundCatalog
        class OverrideStarterCardsCatalog
        class CardCatalogDefinition {
            +id: String
            +catalog: CardCatalog
        }
        class CardCatalogResolver {
            +get(id: String) CardCatalog?
            +getCatalogList() List~CardCatalog~
        }
        class StandardCatalog {
            <<object>>
        }
        class CardType {
            <<enum>>
        }
        class CardIcon {
            <<enum>>
        }
        class NatureCard
        class RestaurantCard
        class MediumEnterpriseCard
        class StadiumCard
        class TVCentreCard
        class BusinessCentreCard
        class RailwayStationCard
        class ShoppingCentreCard
        class TVTowerCard
        class EntertainmentParkCard
    }

    namespace CoreGame {
        class Game {
            +catalog: CardCatalog
            +players: List~Player~
            +gameFinishedTrigger: Triggerable
            +inputEffects: InputEffectsQueue
            +resources: ClassMap
            +orderManager: PlayerOrderManager
            +steps: MutableList~StepPhase~
            +finished: Boolean
            +winner: Player?
            +currentStepPhase: StepPhase?
            +currentPlayer: Player?
            +getTriggerables() Sequence~Pair~Triggerable, Player?~~
            +countCardsOfKind(id: String) Int
            +nextStep() StepPhase
            +addEffectObserver(observer) void
            +notifyEffectApplied(effect: Effect, stepPhase: StepPhase) void
        }
        class SetWinnerEffect
        class Player {
            +name: String
            +balance: Int
            +cards: MutableList~Card~
        }
        class StepPhase {
            <<abstract>>
            +game: Game
            +currentPlayer: Player
            +stepNumber: Int
            +results: ClassMap
            +canBeFinished() Boolean
            +runTriggerables() void
        }
        class PendingStepPhase {
            +submitPlayerAction(action: PlayerAction) FinishedStepPhase?
            +finish() FinishedStepPhase
        }
        class FinishedStepPhase
        class DiceRollResult {
            +player: Player
            +diceThrown: List~Int~
        }
        class IntermediateRollResult {
            +result: DiceRollResult
        }
        class ClassMap
        class InputEffectsQueue {
            +enqueue(effect: InputEffect~*~) void
            +addToEnd(effect: InputEffect~*~) void
            +peek() InputEffect~*~?
            +dequeue(effect: InputEffect~*~) void
            +hasEffect(effect: InputEffect~*~) Boolean
            +toList() List~InputEffect~*~~
        }
        class PlayerOrderManager {
            +players: List~Player~
            +next() Player
            +setNext(player: Player) void
        }
    }

    namespace GameFacility {
        class GameDriver {
            +game: Game
            +observeEffects(observer) void
            +nextStep() PendingStepPhase
            +rollDice(player: Player, numDice: Int) PendingStepPhase
            +needsRethrowDecision(player: Player) Boolean
            +needsRollDecision(player: Player) Boolean
            +needsPickAndChargeDecision(player: Player) Boolean
            +needsSwapCardsDecision(player: Player) Boolean
            +needsAdditionalStepDecision(player: Player) Boolean
            +needsBuyCardDecision(player: Player) Boolean
            +submitRethrowDecision(player: Player, shouldRethrow: Boolean) PendingStepPhase
            +pickAndChargePlayer(player: Player, targetPlayer: Player) PendingStepPhase
            +swapCards(player: Player, input: SwapCardsInput) PendingStepPhase
            +submitAdditionalStepDecision(player: Player, shouldTakeAdditionalStep: Boolean) PendingStepPhase
            +buyCard(player: Player, cardId: String) FinishedStepPhase?
            +skipCardPurchase(player: Player) FinishedStepPhase?
        }
        class GameFactory {
            <<object>>
            +createGame(catalog: CardCatalog, playerNames: List~String~) Game
            +createDriver(catalog: CardCatalog, playerNames: List~String~) GameDriver
            +createDriver(payload: GamePayload) GameDriver
        }
        class GamePayload {
            +players: List~PlayerPayload~
            +metadata: GamePayloadMetadata?
            +catalogResolver: CardCatalogResolver
            +catalog: CardCatalog
            +catalogId: String?
        }
        class GamePayloadMetadata {
            +winner: String?
            +catalogId: String?
            +finished: Boolean?
        }
        class PlayerPayload {
            +name: String
            +balance: Int
            +cards: List~String~
        }
        class JSONExporter {
            +export(game: GamePayload) String
        }
        class JSONImporter {
            +import(content: String) GamePayload
        }
    }

    namespace Storage {
        class StorageBackend {
            <<enum>>
            +cliName: String
            +next() StorageBackend
            +parse(raw: String) StorageBackend?
        }
        class GameStorage {
            <<abstract>>
            +save(name: String, game: GameDriver, catalogId: String) void
            +load(name: String) StoredGame
            +list() List~SavedGameSummary~
            +delete(name: String) void
            +top() List~TopEntry~
        }
        class GameStorageFactory {
            <<object>>
            +create(backend: StorageBackend, root: String, defaultCatalogId: String, catalogResolver: CardCatalogResolver) GameStorage
            +json(root: String, defaultCatalogId: String, catalogResolver: CardCatalogResolver, fileExtension: String) GameStorage
            +h2(databasePath: String, defaultCatalogId: String, catalogResolver: CardCatalogResolver) GameStorage
        }
        class JsonGameStorage
        class SQLStorage
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
        class TopEntry {
            +playerName: String
            +wins: Int
        }
    }

    namespace CLI {
        class CLIApplication {
            +run() void
        }
        class Command {
            <<abstract>>
            +name: String
            +mode: CLIMode
            +matches(commandName: String) Boolean
            +execute(arguments: List~String~, context: CommandContext) void
        }
        class CommandContext {
            +io: CLIIO
            +storage: GameStorage
            +storageBackend: StorageBackend
            +session: CLISession
            +autoAdvance: GameAutoAdvance
            +catalogs: CLICatalogRegistry
            +localiser: Localiser
            +switchStorage(backend: StorageBackend) void
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
        class CLIMode {
            <<enum>>
        }
        class CLIIO {
            <<interface>>
            +readLine() String?
            +writeLine(text: String) void
        }
        class GameAutoAdvance
        class CLICatalogRegistry
    }

    namespace DesktopGUI {
        class AppViewModel {
            +uiState: AppUiState
            +loadGame(id: String) GameDriver
            +top() List~TopEntry~
            +openTop() void
            +saveGame(id: String, catalogId: String, driver: GameDriver) void
            +createGame(name: String, players: List~String~) void
            +openGame(gameId: String) void
            +openManagement() void
            +refreshSavedGames() void
            +toggleStorage() void
            +switchStorage(backend: StorageBackend) void
        }
        class AppUiState {
            +currentScreen: Screen
            +currentGame: ActiveGameSession?
            +savedGames: List~SavedGameSummary~
            +localiser: Localiser
            +storageBackend: StorageBackend
        }
        class ActiveGameSession {
            +id: String
        }
        class GameViewModel {
            +driver: GameDriver
        }
        class Screen {
            <<enum>>
        }
    }

    Trigger <|-- Triggerable
    Trigger <|-- AnyDiceTrigger
    Trigger <|-- PlayerDiceTrigger
    Trigger <|-- PossessorDiceTrigger
    Trigger <|-- BooleanTrigger
    Trigger <|-- AndTrigger
    Trigger <|-- OrTrigger
    Triggerable <|-- SightsCollectedTrigger
    Triggerable <|-- Card

    Effect <|-- CompoundEffect
    Effect <|-- NoopEffect
    Effect <|-- MaybeEffect
    Effect <|-- MoneyTransferEffect
    Effect <|-- FineEffect
    Effect <|-- GrantCardEffect
    Effect <|-- RemoveCardEffect
    Effect <|-- GivePlayerAdditionalStepEffect
    Effect <|-- AwaitInputEffect
    Effect <|-- ProvideInputEffect
    Effect <|-- SetWinnerEffect
    InputEffect <|-- DiceRollInputEffect
    InputEffect <|-- RethrowDiceInputEffect
    InputEffect <|-- BuyCardInputEffect
    InputEffect <|-- PickAndChargeUserInputEffect
    InputEffect <|-- GivePlayerAdditionalStepInputEffect
    InputEffect <|-- SwapCardsInputEffect

    PlayerAction <|-- BuyCardAction
    PlayerAction <|-- BuyCardInputAction
    PlayerAction <|-- ProvideRethrowDecisionAction
    PlayerAction <|-- PickAndChargePlayerAction
    PlayerAction <|-- SwapCardsAction
    PlayerAction <|-- ProvideAdditionalStepDecisionAction

    Card <|-- NatureCard
    Card <|-- RestaurantCard
    Card <|-- MediumEnterpriseCard
    Card <|-- StadiumCard
    Card <|-- TVCentreCard
    Card <|-- BusinessCentreCard
    Card <|-- RailwayStationCard
    Card <|-- ShoppingCentreCard
    Card <|-- TVTowerCard
    Card <|-- EntertainmentParkCard
    CardCatalog <|-- CompoundCatalog
    CardCatalog <|-- OverrideStarterCardsCatalog
    StandardCatalog --|> CardCatalog

    StepPhase <|-- PendingStepPhase
    StepPhase <|-- FinishedStepPhase

    GameStorage <|-- JsonGameStorage
    GameStorage <|-- SQLStorage

    Card --> CardType
    Card --> CardIcon
    CardCatalogResolver --> CardCatalogDefinition
    CardCatalogDefinition --> CardCatalog
    Game --> CardCatalog
    Game --> Triggerable : gameFinishedTrigger
    Game "1" o-- "*" Player : players
    Player "1" o-- "*" Card : cards
    Game "1" o-- "*" StepPhase : steps
    Game --> InputEffectsQueue
    Game --> PlayerOrderManager
    Game --> ClassMap : resources
    StepPhase --> ClassMap : results
    DiceRollResult --> Player
    IntermediateRollResult --> DiceRollResult
    InputEffectsQueue ..> InputEffect : stores
    PlayerOrderManager --> Player

    Trigger ..> StepPhase
    Trigger ..> Player
    Triggerable ..> Effect : returns
    Effect ..> StepPhase
    InputEffect ..> Effect : getEffect
    InputEffect --> Player
    AwaitInputEffect --> InputEffect : targetEffect
    ProvideInputEffect --> InputEffect : effect
    ProvideInputEffect --> Player : fromPlayer
    SwapCardsInput --> Player
    SwapCardsInput --> Card

    MoneyTransferEffect --> MoneyTransferType
    MoneyTransferEffect --> Player
    FineEffect --> Player
    GrantCardEffect --> Player
    GrantCardEffect --> Card
    RemoveCardEffect --> Player
    RemoveCardEffect --> Card
    GivePlayerAdditionalStepEffect --> Player
    PickAndChargeUserInputEffect --> Player
    SwapCardsInputEffect --> Player

    PlayerAction --> Player
    PlayerAction ..> Effect : returns
    BuyCardAction --> Card
    BuyCardInputAction ..> BuyCardInputEffect
    ProvideRethrowDecisionAction ..> RethrowDiceInputEffect
    PickAndChargePlayerAction ..> PickAndChargeUserInputEffect
    SwapCardsAction ..> SwapCardsInputEffect
    ProvideAdditionalStepDecisionAction ..> GivePlayerAdditionalStepInputEffect

    PendingStepPhase ..> PlayerAction : submitPlayerAction
    GameDriver --> Game
    GameDriver ..> DiceRollInputEffect : rollDice
    GameDriver ..> PlayerAction : finishStep
    GameDriver ..> InputEffect : needs decisions
    GameFactory ..> Game : creates
    GameFactory ..> GameDriver : creates
    GameFactory ..> GamePayload : imports

    GamePayload --> PlayerPayload
    GamePayload --> GamePayloadMetadata
    GamePayload --> CardCatalogResolver
    GamePayload ..> CardCatalog : resolves
    JSONExporter ..> GamePayload : serializes
    JSONImporter ..> GamePayload : parses

    GameStorage --> CardCatalogResolver
    GameStorage ..> GamePayload
    GameStorage ..> GameFactory
    GameStorage --> StoredGame
    GameStorage --> SavedGameSummary
    GameStorage --> TopEntry
    GameStorageFactory --> StorageBackend
    GameStorageFactory ..> JsonGameStorage
    GameStorageFactory ..> SQLStorage
    JsonGameStorage --> JSONImporter
    JsonGameStorage --> JSONExporter
    StoredGame --> GameDriver

    CLIApplication --> CommandContext
    CLIApplication --> Command
    CommandContext --> CLIIO
    CommandContext --> GameStorage
    CommandContext --> StorageBackend
    CommandContext --> CLISession
    CommandContext --> GameAutoAdvance
    CommandContext --> CLICatalogRegistry
    Command ..> CommandContext
    CLISession --> ActiveCliGame
    ActiveCliGame --> GameDriver

    AppViewModel --> AppUiState
    AppViewModel --> GameStorage
    AppViewModel --> StorageBackend
    AppViewModel ..> GameFactory
    AppViewModel ..> GameDriver
    AppUiState --> Screen
    AppUiState --> ActiveGameSession
    AppUiState --> SavedGameSummary
    GameViewModel --> GameDriver
```

Для описания дальнейшей логики используется диаграмма классов выше. Далее перечислены моменты.
