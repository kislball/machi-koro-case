package ru.kislball.machikoro.cli.error

class CLIException(
    val key: String,
    val payload: Any = Unit,
    cause: Throwable? = null,
) : IllegalArgumentException(cause)
