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

@Composable
fun AdditionalStepPrompt(
    playerName: String,
    onSelect: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.width(920.dp)) {
        Column {
            Text("$playerName, выпал дубль. Вы походить ещё раз")
            Row {
                Button(onClick = { onSelect(true) }) { Text("Хочу") }
                Button(onClick = { onSelect(false) }) { Text("Пропуск") }
            }
        }
    }
}