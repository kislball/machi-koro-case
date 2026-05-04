package ru.kislball.machikoro.gui.game.prompts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.kislball.machikoro.gui.localisation.LocalAppLocaliser

@Composable
fun AdditionalStepPrompt(
    playerName: String,
    onSelect: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
  val localiser = LocalAppLocaliser.current
  Card(modifier = modifier.width(920.dp)) {
    Column {
      Text(localiser.localise("gui.game.prompt.additional_step.title", playerName))
      Row {
        Button(onClick = { onSelect(true) }) {
          Text(localiser.localise("gui.game.prompt.additional_step.confirm"))
        }
        Button(onClick = { onSelect(false) }) {
          Text(localiser.localise("gui.game.prompt.additional_step.decline"))
        }
      }
    }
  }
}
