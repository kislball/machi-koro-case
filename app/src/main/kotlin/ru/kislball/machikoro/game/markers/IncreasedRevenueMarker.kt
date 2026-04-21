package ru.kislball.machikoro.game.markers

import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.utilities.contains
import ru.kislball.machikoro.game.utilities.get

private class IncreasedRevenueMarker {
  private val bonuses: MutableMap<CardIcon, Int> = mutableMapOf()

  fun getBonus(card: CardIcon) = bonuses[card] ?: 0

  fun setBonus(card: CardIcon, bonus: Int) {
    bonuses[card] = bonus
  }
}

private fun Player.getRevenueMarker(): IncreasedRevenueMarker {
  if (!resources.contains<IncreasedRevenueMarker>()) {
    resources.set(IncreasedRevenueMarker())
  }
  return resources.get<IncreasedRevenueMarker>()
}

fun Player.getBonusForType(type: CardIcon): Int {
  return getRevenueMarker().getBonus(type)
}

fun Player.setBonusForType(type: CardIcon, bonus: Int) {
  getRevenueMarker().setBonus(type, bonus)
}
