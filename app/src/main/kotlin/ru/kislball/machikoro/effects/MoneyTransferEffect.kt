package ru.kislball.machikoro.effects

import kotlin.math.max
import kotlin.math.min
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step

enum class MoneyTransferType {
  Deposit,
  Withdraw,
  WithdrawExact,
}

class MoneyTransferEffect(val player: Player, val amount: Int, val type: MoneyTransferType) :
    Effect() {
  override var effectDescriptionKey: String = "effects.money-transfer.description"

  override var effectNameKey: String = "effects.money-transfer.name"

  override fun apply(step: Step) {
    when (type) {
      MoneyTransferType.Deposit -> player.balance += amount
      MoneyTransferType.Withdraw -> player.balance = max(player.balance - amount, 0)
      MoneyTransferType.WithdrawExact -> {
        if (player.balance >= amount) {
          player.balance -= amount
        } else {
          throw IllegalArgumentException("Insufficient funds")
        }
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
