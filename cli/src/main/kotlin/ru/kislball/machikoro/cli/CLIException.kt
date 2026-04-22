package ru.kislball.machikoro.cli

class CLIException(val key: String, val payload: Any = Unit) : IllegalArgumentException()
