package ru.kislball.machikoro.exceptions

sealed class GameException(val key: String) : RuntimeException()

open class GameStateException(key: String) : GameException(key)

open class PlayerException(key: String) : GameException(key)

open class DiceException(key: String) : GameStateException(key)

open class CardException(key: String) : GameException(key)

open class StepException(key: String) : GameStateException(key)

open class EffectException(key: String) : GameException(key)

open class SwapException(key: String) : GameException(key)

open class InputException(key: String) : GameException(key)

open class LocalisationException(key: String) : GameException(key)

class PlayerNotFoundException(val playerName: String) :
    PlayerException("exception.player.not_found")

class InsufficientFundsException(val playerName: String) :
    PlayerException("exception.insufficient_funds")

class DiceNotRolledException : DiceException("exception.dice.not_rolled")

class GameFinishedException : GameStateException("exception.game.finished")

class PlayerNotCurrentException(val playerName: String) :
    PlayerException("exception.player.not_current")

class InvalidDiceRollCountException(val playerName: String) :
    DiceException("exception.dice.invalid_count")

class InvalidDiceRollInputException(val input: Int) : DiceException("exception.dice.invalid_input")

class DiceAlreadyRolledException : DiceException("exception.dice.already_rolled")

class RethrowDecisionPendingException : DiceException("exception.rethrow.pending")

class InputEffectPendingException : InputException("exception.input.pending")

class CardNotFoundException(val cardId: String) : CardException("exception.card.not_found")

class NotEnoughCardsException(val cardId: String) : CardException("exception.card.not_enough")

class StepNotFinishableException : StepException("exception.step.not_finishable")

class PlayerDoesNotHaveCardException(val playerName: String, val cardId: String) :
    PlayerException("exception.player.card_missing")

open class InvalidSwapException(key: String) : SwapException(key)

class PlayerCannotSwapWithSelfException : SwapException("exception.swap.self")

class SightsCannotBeExchangedException : SwapException("exception.swap.sights")

class SpecialCardsCannotBeExchangedException : SwapException("exception.swap.special")

class PlayerAlreadyAddedException(val playerName: String) :
    PlayerException("exception.player.already_added")

class PlayerNameBlankException : PlayerException("exception.player.name_blank")

class PlayerNamesNotUniqueException : PlayerException("exception.player.names_not_unique")

class DuplicateCardsException : CardException("exception.card.duplicate")

class EmptyPlayersListException : PlayerException("exception.player_list.empty")

class EffectNotValidException(val effectId: String) : EffectException("exception.effect.invalid")

class EffectInputNotValidException(val effectId: String, val input: String) :
    EffectException("exception.effect.input_invalid")

class EffectInputTypeMismatchException(val effectId: String) :
    EffectException("exception.effect.input_type_mismatch")

class InvalidStepSubstitutionException : StepException("exception.step.substitution_invalid")

class CurrentStepNotReadyException : StepException("exception.step.not_ready")

class WaitingStepTypeMismatchException(
    val expected: String,
    val actual: String?
) : StepException("exception.step.type_mismatch")

class EffectNotAwaitingInputException(val effectId: String) :
    EffectException("exception.effect.not_awaiting_input")

class InputProvidedByNonOwnerException : InputException("exception.input.non_owner")

class AwaitingInputEffectMismatchException(val expected: String) :
    InputException("exception.input.effect_mismatch")

class PossessorNotSetException : GameException("exception.possessor.not_set")

class NoFillerCardsAvailableException : GameException("exception.no_filler_cards")

class LocalisationKeyNotFoundException(key: String) :
    LocalisationException("exception.localisation.key_not_found")

class InvalidLocalisationInputException(key: String) :
    LocalisationException("exception.localisation.input_invalid")

