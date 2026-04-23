package ru.kislball.machikoro.cli.command

import ru.kislball.machikoro.cli.catalog.CLICatalogRegistry
import ru.kislball.machikoro.cli.game.GameAutoAdvance
import ru.kislball.machikoro.cli.io.CLIIO
import ru.kislball.machikoro.cli.localiser.RussianCLILocaliser
import ru.kislball.machikoro.cli.session.CLISession
import ru.kislball.machikoro.cli.storage.CLIStorage
import ru.kislball.machikoro.localisation.CompoundLocaliser
import ru.kislball.machikoro.localisation.Localiser
import ru.kislball.machikoro.localisation.RussianLocaliser

class CommandContext(
    val io: CLIIO,
    val storage: CLIStorage,
    val session: CLISession,
    val autoAdvance: GameAutoAdvance,
    val catalogs: CLICatalogRegistry,
    val localiser: Localiser =
        CompoundLocaliser(
            primary = RussianCLILocaliser(),
            fallback = RussianLocaliser(),
        ),
) {
  val defaultCatalogId: String
    get() = catalogs.defaultCatalogId

  val defaultCatalog
    get() = catalogs.require(defaultCatalogId).catalog

  fun printLine(key: String, payload: Any = Unit) {
    io.writeLine(localiser.localise(key, payload))
  }

  fun printRaw(text: String) {
    io.writeLine(text)
  }
}
