package ru.kislball.machikoro.game

abstract class Step(val game: Game, val currentPlayer: Player, val stepNumber: Int) {
  protected var finalised = false
  private var activated = false

  protected fun runTriggerables() {
    for ((triggerable, player) in game.getTriggerables()) {
      triggerable.apply(this, player)
    }
  }

  fun canBeFinished(): Boolean {
    return !finalised && game.currentStep == this
  }

  protected fun <T : Step> substitute(s: T): T {
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
