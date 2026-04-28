package ru.kislball.machikoro.gui.localisation

import androidx.compose.runtime.staticCompositionLocalOf
import ru.kislball.machikoro.localisation.Localiser

val LocalAppLocaliser = staticCompositionLocalOf<Localiser> { russianDesktopLocaliser() }
