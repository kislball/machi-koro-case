package ru.kislball.machikoro.effects.money

import ru.kislball.machikoro.effects.Effect
import kotlin.math.min
import ru.kislball.machikoro.effects.utility.CompoundEffect
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

class FineEffect(
    val from: Player,
    val to: Player,
    val amount: Int,
) : Effect("effects.fine") {
  fun getFinalAmount(): Int {
    return min(amount, from.balance)
  }

  private fun getInnerEffect(): Effect {
    return CompoundEffect.combineEffects(
        MoneyTransferEffect(
            player = from, type = MoneyTransferType.WithdrawExact, amount = getFinalAmount()),
        MoneyTransferEffect(
            player = to, type = MoneyTransferType.Deposit, amount = getFinalAmount()),
    )
  }

    override fun isValid(stepPhase: StepPhase): Boolean {
        return getInnerEffect().isValid(stepPhase)
    }

  override fun run(stepPhase: StepPhase) {
    return getInnerEffect().apply(stepPhase)
  }
}
