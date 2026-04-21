package ru.kislball.machikoro.effects.money

import kotlin.math.max
import kotlin.math.min
import ru.kislball.machikoro.effects.Effect
import ru.kislball.machikoro.exceptions.InsufficientFundsException
import ru.kislball.machikoro.exceptions.require
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.step.StepPhase

enum class MoneyTransferType {
  Deposit,
  Withdraw,
  WithdrawExact,
}

class MoneyTransferEffect(val player: Player, val amount: Int, val type: MoneyTransferType) :
    Effect("effects.money.transfer") {

  override fun isValid(stepPhase: StepPhase): Boolean {
    if (type == MoneyTransferType.WithdrawExact) {
      require(player.balance >= amount) { InsufficientFundsException(player.name) }
    }
    return true
  }

  override fun run(stepPhase: StepPhase) {
    when (type) {
      MoneyTransferType.Deposit -> player.balance += amount
      MoneyTransferType.Withdraw -> player.balance = max(player.balance - amount, 0)
      MoneyTransferType.WithdrawExact -> {
        player.balance -= amount
      }
    }
  }

  fun getChange(): Int {
    return when (type) {
      MoneyTransferType.Deposit -> amount
      MoneyTransferType.Withdraw -> min(player.balance, amount)
      MoneyTransferType.WithdrawExact ->
          if (player.balance >= amount) {
            amount
          } else {
            0
          }
    }
  }
}
