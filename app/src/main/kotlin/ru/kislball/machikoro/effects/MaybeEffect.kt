package ru.kislball.machikoro.effects

import ru.kislball.machikoro.game.Step

class MaybeEffect(val inner: Effect) : Effect() {
    override var effectDescriptionKey = inner.effectDescriptionKey
    override var effectNameKey = inner.effectNameKey

    override fun apply(step: Step) {
        try {
            inner.apply(step)
        } catch (e: Exception) {
            print("${this.javaClass.simpleName}: effect ${inner.javaClass.simpleName} emitted error ${e.message}")
        }
    }
}
