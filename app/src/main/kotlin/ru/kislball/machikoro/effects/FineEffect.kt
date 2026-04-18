package ru.kislball.machikoro.effects

import kotlin.math.min
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step

class FineEffect(
    val from: Player,
    val to: Player,
    val amount: Int,
) : Effect() {
  override var effectDescriptionKey: String = "effects.fine.description"
  override var effectNameKey: String = "effects.fine.name"

  fun getFinalAmount(): Int {
    return min(amount, from.balance)
  }

  private fun getInnerEffect(): Effect {
    return CompoundEffect.combineEffects(
        MoneyTransferEffect(
            player = from, type = MoneyTransferType.WithdrawExact, amount = getFinalAmount()),
        MoneyTransferEffect(player = to, type = MoneyTransferType.Deposit, amount = getFinalAmount()),
    )
  }

  override fun apply(step: Step) {
    return getInnerEffect().apply(step)
  }
}
