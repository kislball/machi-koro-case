package ru.kislball.machikoro.gui.game.prompts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RethrowPrompt(
    playerName: String,
    onSelect: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
  Card(modifier = modifier.width(340.dp)) {
    Column(modifier = Modifier.padding(10.dp)) {
      Text("$playerName, хотите перебросить кости?")
      Row {
        Button(onClick = { onSelect(true) }) { Text("Перебросить") }
        Button(onClick = { onSelect(false) }) { Text("Оставить") }
      }
    }
  }
}
