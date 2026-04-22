package ru.kislball.machikoro.facility

import kotlin.test.Test
import kotlin.test.assertContains
import ru.kislball.machikoro.cards.standard.StandardCatalog

class GameDriverObserverTest {
  @Test
  fun `observer receives dice effect application`() {
    val driver = GameFactory.createDriver(StandardCatalog, listOf("alice"))
    val observed = mutableListOf<String>()
    driver.observeEffects { effect, _ -> observed.add(effect.id) }

    driver.rollDice(driver.game.players.single(), 1)

    assertContains(observed, "effects.dice.roll")
  }

  @Test
  fun `observer receives nested effects from buy card`() {
    val driver = GameFactory.createDriver(StandardCatalog, listOf("alice"))
    val player = driver.game.players.single().apply { balance = 10 }
    val observed = mutableListOf<String>()
    driver.observeEffects { effect, _ -> observed.add(effect.id) }

    driver.rollDice(player, 1)
    driver.buyCard(player, "cards.wheat")

    assertContains(observed, "effects.money.transfer")
    assertContains(observed, "effects.cards.grant")
  }
}
