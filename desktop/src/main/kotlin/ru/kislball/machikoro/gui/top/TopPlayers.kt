package ru.kislball.machikoro.gui.top

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.kislball.machikoro.storage.TopEntry

@Composable
fun TopPlayerEntry(
    name: String,
    totalWins: Int,
    position: Int,
) {
  Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()) {
        Text("$position. $name")
        Row {
          Text("$totalWins")
          Icon(Icons.Default.Star, contentDescription = null)
        }
      }
}

@Composable
fun TopPlayers(
    entries: List<TopEntry>,
    onReturn: () -> Unit,
) {
  val scrollState = rememberScrollState()

  Box(modifier = Modifier.fillMaxSize()) {
    IconButton(onClick = onReturn, modifier = Modifier.align(Alignment.TopStart)) {
      Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
    }
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text("Топ игроков", fontWeight = FontWeight.W600, fontSize = 24.sp)
      Spacer(Modifier.height(10.dp))
      Column(
          modifier = Modifier.verticalScroll(scrollState).fillMaxHeight(0.7F).fillMaxWidth(0.9F)) {
            entries.forEachIndexed { index, topEntry ->
              TopPlayerEntry(topEntry.playerName, topEntry.wins, index + 1)
            }
          }
    }
  }
}
