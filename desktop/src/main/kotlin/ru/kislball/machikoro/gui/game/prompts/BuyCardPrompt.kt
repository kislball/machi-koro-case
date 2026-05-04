package ru.kislball.machikoro.gui.game.prompts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.kislball.machikoro.cards.common.Card as GameCard
import ru.kislball.machikoro.gui.cards.CardImage

data class BuyCardOptionUi(
    val card: GameCard,
    val cardId: String,
    val title: String,
    val priceLabel: String,
    val remainingLabel: String,
    val enabled: Boolean,
)

@Composable
fun BuyCardPrompt(
    options: List<BuyCardOptionUi>,
    title: String,
    description: String,
    skipLabel: String,
    onSelect: (String) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Card(modifier = modifier.width(920.dp)) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
          text = title,
          style = MaterialTheme.typography.headlineSmall,
          textAlign = TextAlign.Center,
      )
      Spacer(Modifier.height(8.dp))
      Text(
          text = description,
          style = MaterialTheme.typography.bodyMedium,
          textAlign = TextAlign.Center,
      )
      Spacer(Modifier.height(16.dp))
      LazyRow(
          horizontalArrangement = Arrangement.spacedBy(16.dp),
          contentPadding = PaddingValues(horizontal = 16.dp),
      ) {
        items(options, key = BuyCardOptionUi::cardId) { option ->
          OutlinedButton(
              onClick = { onSelect(option.cardId) },
              enabled = option.enabled,
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              CardImage(card = option.card)
              Spacer(Modifier.height(8.dp))
              Text(option.title, textAlign = TextAlign.Center)
              Text(option.priceLabel, style = MaterialTheme.typography.bodySmall)
              Text(option.remainingLabel, style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      }
      Spacer(Modifier.height(16.dp))
      TextButton(onClick = onSkip) { Text(skipLabel) }
    }
  }
}
