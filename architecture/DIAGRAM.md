```mermaid
classDiagram
    namespace Triggers { class Trigger {
	    String +triggerDescriptionKey$;
	    String +triggerNameKey$;
	    +isTriggered(StepContext sc) void*;
    }
    
    class AnyDiceTrigger {
	    List~int~ +diceValues;
    }
    class PlayerDiceTrigger {
	    List~int~ +diceValues;
	    Player +player;
    }
	class AndTrigger {
		List~Trigger~ +triggers;
	}
	class OrTrigger {
		List~Trigger~ +triggers;
	} }
    
    AnyDiceTrigger ..|> Trigger
    AndTrigger ..|> Trigger
    OrTrigger ..|> Trigger
    PlayerDiceTrigger ..|> Trigger
    
    namespace Effects {
        class Effect {
            String +effectDescriptionKey$;
            String +effectNameKey$;
            +apply(StepContext sc) void*;
        }

        class CompoundEffect {
            List~Effect~ +effects;
        }

        class MoneyTransferEffect {
            Player +from;
            Player +to;
            int +amount;
        }

        class BuyCardEffect {
            Player +player;
            CardFactory +factory;
        }
    note for Effect "Invariant validation on creation by effects"
    }

    
    BuyCardEffect ..> CardFactory : uses
    
    CompoundEffect ..|> Effect
    BuyCardEffect ..|> Effect
	MoneyTransferEffect ..|> Effect 
   
    namespace Actions { class PlayerAction {
	    Player +player;
	    +getEffects() List~Effect~;
    }
    class BuyAction {
	    
    } }
    
    PlayerAction ..> Effect : creates

    namespace Cards { class Card {
	    String +cardDescriptionKey$;
	    String +cardNameKey$;
	    +getPrice(Step sc) int*;
	    +getEffect(Step sc) Effect*;
    }
    class SightCard
    class EnterpriseCard
    
    class CardFactory {
	    +create() Card;
    } }
    CardFactory ..> Card : creates
    
    
    BuyAction --|> PlayerAction
    BuyAction --|> BuyCardEffect
    
    namespace CoreGame { class Step {
	    Game +game;
	    Player +currentPlayer;
	    int +stepNumber;
		+rollDice(int numDice) DiceRolledStep;	    
    }
    
    class DiceRolledStep {
	    List~int~ +diceRolls;
	    List~Effect~ +intermediateEffects;
	    +finish(PlayerAction action) FinishedStep;
    }

	class FinishedStep {
		PlayerAction +action;
		List~Effect~ effectsApplied;
	}
	
    
    class Game {
	    List~Player~ +players;
	    Step? +currentStep;
	    List~Step~ +steps;
	    +nextStep() Step;
    }
    
    
    
    class Player {
	    String +name;
	    List~Card~ +cards;
	    Game +game;
    } }

	Step <|-- DiceRolledStep
	DiceRolledStep <|-- FinishedStep
    Game ..> Step : creates
	Step ..> DiceRolledStep : creates
	DiceRolledStep ..> FinishedStep : creates

    JSONExporter ..|> GameExporter
    CSVExporter ..|> GameExporter
    JSONImporter ..|> GameImporter
    CSVImporter ..|> GameImporter
    
    namespace GameFacility { class GameExporter {
	    +export(Game g) String;
    }
    
    class GameImporter {
	    +import(String s) Game;
    }

    class GameDriver {
        Game +game; 
    }
    class JSONExporter {}
    class JSONImporter {}
    class CSVExporter {}
    class CSVImporter {}

    note for GameFactory "Game logic entrypoint"
    class GameFactory {
    } }
    

    GameFactory ..> Game : creates
    GameFactory ..> GameExporter : uses
    GameFactory ..> GameImporter : uses
    GameFactory ..> GameDriver : creates


    
    Player ..> Card : posseses
    Game ..> Player : contains

    Effect <|.. Card : creates

    Trigger <|.. Card
    Effect ..> Game : changes

    Card <|.. SightCard
    Card <|.. EnterpriseCard
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

























