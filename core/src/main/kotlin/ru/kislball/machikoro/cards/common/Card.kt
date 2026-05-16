package ru.kislball.machikoro.cards.common

import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase
import ru.kislball.machikoro.game.utilities.Triggerable

abstract class Card(
    val cardId: String,
    val type: CardType,
    val totalCards: Int = 4,
    val icon: CardIcon,
) : Triggerable("card.$cardId") {
  abstract fun getPrice(s: StepPhase): Int

  open fun canPurchase(player: Player): Boolean = true

  open val cardNameKey: String
    get() = "$cardId.name"

  open val cardDescriptionKey
    get() = "$cardId.description"
}
