package ru.kislball.machikoro.gui.game.prompts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DiceInputPrompt(
    canRollTwoDice: Boolean,
    playerName: String,
    onSelect: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    Text(
        "$playerName, бросьте кости",
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp, 0.dp),
        horizontalArrangement = Arrangement.Center) {
          if (canRollTwoDice) {
            Button(onClick = { onSelect(1) }) { Text("Бросить одну кость") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { onSelect(2) }) { Text("Бросить две кости") }
          } else {
            Button(onClick = { onSelect(1) }) { Text("Бросить кости") }
          }
        }
  }
}

@Composable
@Preview
fun DiceInputPromptPreview() {
  Column {
    DiceInputPrompt(true, "Test")
    Spacer(Modifier.height(8.dp))
    DiceInputPrompt(false, "Test")
  }
}
