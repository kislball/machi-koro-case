package ru.kislball.machikoro.effects

import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.Step

class MoneyTransferEffect(val from: Player?, val to: Player?, val amount: Int) : Effect() {
  override var effectDescriptionKey: String = "effects.money-transfer.description"

  override var effectNameKey: String = "effects.money-transfer.name"

  override fun apply(step: Step) {
    if (from != null) {
      check(from.balance >= amount) { "Not enough balance" }
      from.balance -= amount
    }
    to?.balance += amount
  }
}
