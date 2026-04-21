package ru.kislball.machikoro.exceptions

fun require(value: Boolean, exception: () -> GameException) {
  if (!value) throw exception()
}

fun check(value: Boolean, exception: () -> GameException) {
  if (!value) throw exception()
}
