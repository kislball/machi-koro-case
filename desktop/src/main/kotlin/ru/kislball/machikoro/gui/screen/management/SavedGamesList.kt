package ru.kislball.machikoro.gui.screen.management

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

data class SavedGame(
    val name: String,
    val playerNames: List<String>,
    val isFinished: Boolean,
    val createdAt: Date,
)

@Composable
fun SavedGameListItem(
    savedGame: SavedGame,
    onClick: () -> Unit,
) {
  Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.fillMaxWidth()
              .pointerHoverIcon(PointerIcon.Hand)
              .clickable(onClick = onClick)
              .padding(16.dp),
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(savedGame.name, fontSize = 24.sp)
        Spacer(Modifier.width(8.dp))
        Text(
            savedGame.createdAt
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
            fontSize = 16.sp)
      }
      Text(savedGame.playerNames.joinToString(", "), fontSize = 16.sp)
    }
    Box(
        modifier =
            Modifier.size(16.dp)
                .background(
                    color =
                        if (savedGame.isFinished) {
                          Color.Red
                        } else {
                          Color.Green
                        },
                    shape = CircleShape),
    )
  }
}

@Composable
@Preview
fun SavedGamePreview() {
  SavedGameListItem(
      savedGame =
          SavedGame(
              name = "test",
              playerNames = listOf("Alice", "Bob", "Chloe", "Dylan"),
              isFinished = false,
              createdAt = Date(),
          ),
      onClick = {},
  )
}
