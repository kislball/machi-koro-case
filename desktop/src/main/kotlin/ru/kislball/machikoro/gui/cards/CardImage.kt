@file:Suppress("FunctionNaming")

package ru.kislball.machikoro.gui.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.kislball.machikoro.cards.common.Card

val CardImageWidth = 160.dp
val CardImageHeight = 240.dp

fun Card.imageResourcePath(): String {
  return "cards/${cardId.removePrefix("cards.")}.png"
}

@Composable
fun CardImage(
    card: Card,
    modifier: Modifier = Modifier,
    contentDescription: String? = card.cardId,
) {
  Image(
      painter = painterResource(card.imageResourcePath()),
      contentDescription = contentDescription,
      contentScale = ContentScale.Fit,
      modifier = modifier.size(CardImageWidth, CardImageHeight),
  )
}
