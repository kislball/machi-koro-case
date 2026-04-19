package ru.kislball.machikoro.game.step

import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player
import ru.kislball.machikoro.game.utilities.ClassMap

abstract class StepPhase(val game: Game, val currentPlayer: Player, val stepNumber: Int) {
  protected var finalised = false
  private var activated = false
  val results = ClassMap()

  internal fun runTriggerables() {
    for ((triggerable, player) in game.getTriggerables()) {
      triggerable.apply(this, player)
    }
  }

  fun canBeFinished(): Boolean {
    return !finalised && game.currentStepPhase == this
  }

  protected fun <T : StepPhase> substitute(s: T): T {
    check(canBeFinished()) { "Substitution impossible - this step can't be finished" }
    game.steps[game.steps.lastIndex] = s
    finalised = true
    s.activate()
    return s
  }

  internal fun activate() {
    if (activated) return
    runTriggerables()
    activated = true
  }
}
