package ru.kislball.machikoro.facility

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import ru.kislball.machikoro.StubCard
import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInput
import ru.kislball.machikoro.effects.cards.swap.SwapCardsInputEffect
import ru.kislball.machikoro.effects.money.PickAndChargeUserInputEffect
import ru.kislball.machikoro.effects.order.GivePlayerAdditionalStepInputEffect
import ru.kislball.machikoro.game.DiceRollResult
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.IntermediateRollResult
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.markers.setCanRethrowDice
import ru.kislball.machikoro.game.utilities.getOrNull

class GameDriverInputActionsTest {
  @Test
  fun `finishStep resolves pending rethrow decision action`() {
    val player = Player("p1")
    player.setCanRethrowDice(true)
    val game = Game(listOf(player))
    val driver = GameDriver(game)

    val rolled = driver.rollDice(player, 1)
    val result = driver.submitRethrowDecision(player, shouldRethrow = false)

    assertEquals(rolled, result)
    assertEquals(rolled, game.currentStepPhase)
    assertNull(game.inputEffects.peek())
    assertNull(rolled.results.getOrNull<IntermediateRollResult>())
    assertEquals(1, rolled.results.getOrNull<DiceRollResult>()!!.diceThrown.size)
  }

  @Test
  fun `finishStep resolves pending pick and charge action`() {
    val owner = Player("owner")
    val target = Player("target")
    owner.balance = 1
    target.balance = 5
    val game = Game(listOf(owner, target))
    val driver = GameDriver(game)
    driver.nextStep()
    game.inputEffects.enqueue(PickAndChargeUserInputEffect(owner, amount = 3))

    val result = driver.pickAndChargePlayer(owner, target)

    assertEquals(game.currentStepPhase, result)
    assertNull(game.inputEffects.peek())
    assertEquals(4, owner.balance)
    assertEquals(2, target.balance)
  }

  @Test
  fun `finishStep resolves pending swap cards action`() {
    val owner = Player("owner")
    val opponent = Player("opponent")
    val ownerCard = StubCard("cards.owner")
    val opponentCard = StubCard("cards.opponent")
    owner.cards.add(ownerCard)
    opponent.cards.add(opponentCard)
    val game = Game(listOf(owner, opponent))
    val driver = GameDriver(game)
    driver.nextStep()
    game.inputEffects.enqueue(SwapCardsInputEffect(owner))

    val result = driver.swapCards(owner, SwapCardsInput(opponent, opponentCard, ownerCard))

    assertEquals(game.currentStepPhase, result)
    assertNull(game.inputEffects.peek())
    assertEquals(listOf<Card>(ownerCard), opponent.cards)
    assertEquals(listOf<Card>(opponentCard), owner.cards)
  }

  @Test
  fun `finishStep resolves pending additional step action`() {
    val p1 = Player("p1")
    val p2 = Player("p2")
    val game = Game(listOf(p1, p2))
    val driver = GameDriver(game)
    driver.nextStep()
    game.inputEffects.enqueue(GivePlayerAdditionalStepInputEffect(p1))

    val result = driver.submitAdditionalStepDecision(p1, shouldTakeAdditionalStep = true)

    assertEquals(game.currentStepPhase, result)
    assertNull(game.inputEffects.peek())
    assertEquals(p1, game.orderManager.peekNext())
  }
}



