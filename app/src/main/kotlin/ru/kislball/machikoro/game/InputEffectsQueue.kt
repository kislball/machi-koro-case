package ru.kislball.machikoro.game

import ru.kislball.machikoro.effects.input.InputEffect

class InputEffectsQueue {
  private val effectsAwaitingInput = mutableListOf<InputEffect<*>>()

  fun enqueue(effect: InputEffect<*>) {
    effectsAwaitingInput.add(effect)
  }

  fun peek(): InputEffect<*>? {
    return effectsAwaitingInput.lastOrNull()
  }

  fun dequeue(effect: InputEffect<*>) {
    effectsAwaitingInput.remove(effect)
  }

  fun hasEffect(effect: InputEffect<*>): Boolean {
    return effectsAwaitingInput.contains(effect)
  }
}
