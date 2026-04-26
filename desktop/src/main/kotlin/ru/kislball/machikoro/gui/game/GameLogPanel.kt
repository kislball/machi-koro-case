package ru.kislball.machikoro.gui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameLogPanel(
    title: String,
    entries: List<String>,
    pendingInputMarker: String? = null,
    modifier: Modifier = Modifier,
) {
  val scrollState = rememberScrollState()

  Column(
      modifier =
          modifier
              .width(320.dp)
              .background(
                  color = MaterialTheme.colorScheme.surfaceVariant,
                  shape = RoundedCornerShape(16.dp))
              .padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        pendingInputMarker?.let { marker ->
          Text(
              marker,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.primary)
          Spacer(Modifier.height(12.dp))
        }
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(scrollState)) {
          entries.takeLast(LOG_ENTRY_LIMIT).forEach { entry ->
            Text(entry, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
          }
        }
      }
}

private const val LOG_ENTRY_LIMIT = 10
