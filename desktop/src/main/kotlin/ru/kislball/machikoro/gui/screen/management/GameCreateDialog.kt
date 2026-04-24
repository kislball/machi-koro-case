package ru.kislball.machikoro.gui.screen.management

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

data class GameCreateSubmission(
    val players: List<String>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCreateDialog(
    name: String,
    onSubmit: (GameCreateSubmission) -> Unit,
    onDismiss: () -> Unit,
) {
  val playerNames = remember { mutableStateListOf<String>() }
  var currentPlayerName by remember { mutableStateOf("") }
  val trimmedPlayerName = currentPlayerName.trim()

  AlertDialog(
      onDismissRequest = onDismiss,
      title = { Text("Создание игры $name") },
      text = {
          Column {
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier.fillMaxWidth()
              ) {
                  TextField(
                      value = currentPlayerName,
                      onValueChange = { currentPlayerName = it },
                      label = { Text("Имя игрока") },
                      modifier = Modifier.fillMaxWidth(0.85F),
                  )
                  Button(
                      onClick = {
                          if (trimmedPlayerName.isNotEmpty()) {
                              playerNames.add(trimmedPlayerName)
                              currentPlayerName = ""
                          }
                      },
                      enabled = trimmedPlayerName.isNotEmpty() && trimmedPlayerName !in playerNames,
                  ) {
                      Icon(
                          imageVector = Icons.Default.Add,
                          contentDescription = "Добавить игрока",
                      )
                  }
              }
              Column {
                  playerNames.forEach { playerName ->
                      PlayerListItem(
                          playerName = playerName,
                          onDelete = { playerNames.remove(playerName) },
                      )
                  }
              }
          }
      },
      confirmButton = {
        Button(onClick = { onSubmit(GameCreateSubmission(playerNames.toList())) }) { Text("OK") }
      },
  )
}

@Composable
fun PlayerListItem(
    playerName: String,
    onDelete: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(playerName)
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Удалить игрока",
            )
        }
    }
}
