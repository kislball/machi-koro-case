package ru.kislball.machikoro.cards.common

import ru.kislball.machikoro.game.utilities.Triggerable
import ru.kislball.machikoro.game.step.StepPhase

abstract class Card(
    val cardId: String,
    val type: CardType,
    val totalCards: Int = 4,
    val icon: CardIcon,
) : Triggerable("card.$cardId") {
  abstract fun getPrice(s: StepPhase): Int

  open val cardNameKey: String
    get() = "$cardId.name"

  open val cardDescriptionKey
    get() = "$cardId.description"
}
