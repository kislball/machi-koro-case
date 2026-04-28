package ru.kislball.machikoro.gui.management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.kislball.machikoro.gui.AppUiState
import ru.kislball.machikoro.gui.AppViewModel
import ru.kislball.machikoro.gui.localisation.LocalAppLocaliser

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameSelectionScreen(
    uiState: AppUiState,
    app: AppViewModel,
) {
  var newGameTitle by remember { mutableStateOf("") }
  val savedGamesScrollState = rememberScrollState()
  var isCreateGameDialogOpen by remember { mutableStateOf(false) }
  val localiser = LocalAppLocaliser.current

  if (isCreateGameDialogOpen) {
    GameCreateDialog(
        name = newGameTitle.trim(),
        onSubmit = { submission ->
          app.createGame(newGameTitle, submission.players)
          app.openGame(newGameTitle)
          isCreateGameDialogOpen = false
          newGameTitle = ""
        },
        onDismiss = { isCreateGameDialogOpen = false },
    )
  }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val savedGamesMaxHeight = maxHeight / 2
    Box(modifier = Modifier.align(Alignment.TopEnd)) {
      IconButton(
          onClick = app::openTop,
      ) {
        Icon(
            Icons.Filled.Leaderboard,
            contentDescription = localiser.localise("gui.management.leaderboard"),
        )
      }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
      Text(localiser.localise("gui.app.title"), fontSize = 24.sp)
      Spacer(Modifier.height(10.dp))
      Spacer(Modifier.height(14.dp))
      FlowRow(
          horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth(),
      ) {
        TextField(
            value = newGameTitle,
            onValueChange = { newGameTitle = it },
            label = { Text(localiser.localise("gui.management.new_game_name")) },
        )
        Button(
            onClick = { isCreateGameDialogOpen = true },
            enabled = newGameTitle.isNotBlank(),
            modifier = Modifier.height(56.dp),
        ) {
          Text(localiser.localise("gui.management.create"))
        }
      }
      Spacer(Modifier.height(18.dp))
      Text(localiser.localise("gui.management.or"), fontSize = 24.sp, color = Color.Gray)
      Spacer(Modifier.height(18.dp))
      Column(
          modifier =
              Modifier.fillMaxWidth(0.9F)
                  .heightIn(max = savedGamesMaxHeight)
                  .verticalScroll(savedGamesScrollState),
      ) {
        uiState.savedGames.forEach { savedGame ->
          SavedGameListItem(
              savedGame = savedGame,
              onClick = { app.openGame(savedGame.name) },
          )
        }
      }
    }
  }
}
