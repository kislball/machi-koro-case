package ru.kislball.machikoro.cli

interface CLIIO {
  fun readLine(): String?

  fun writeLine(text: String)
}

object StdCLIIO : CLIIO {
  override fun readLine(): String? = kotlin.io.readLine()

  override fun writeLine(text: String) {
    println(text)
  }
}
