package ru.kislball.machikoro.cli

import ru.kislball.machikoro.cards.standard.StandardCatalog
import ru.kislball.machikoro.localisation.CompoundLocaliser
import ru.kislball.machikoro.localisation.Localiser
import ru.kislball.machikoro.localisation.RussianLocaliser

class CommandContext(
    val io: CLIIO,
    val storage: CLIStorage,
    val session: CLISession,
    val autoAdvance: GameAutoAdvance,
    val localiser: Localiser =
        CompoundLocaliser(
            primary = RussianCLILocaliser(),
            fallback = RussianLocaliser(),
        ),
) {
  val catalog = StandardCatalog

  fun printLine(key: String, payload: Any = Unit) {
    io.writeLine(localiser.localise(key, payload))
  }

  fun printRaw(text: String) {
    io.writeLine(text)
  }
}
