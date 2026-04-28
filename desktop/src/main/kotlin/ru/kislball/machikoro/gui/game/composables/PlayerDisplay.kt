package ru.kislball.machikoro.gui.game.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.standard.enterprises.BusinessCentreCard
import ru.kislball.machikoro.cards.standard.enterprises.TVCentreCard
import ru.kislball.machikoro.cards.standard.sights.EntertainmentParkCard
import ru.kislball.machikoro.cards.standard.sights.TVTowerCard
import ru.kislball.machikoro.gui.cards.CardImage

enum class PlayerDisplayAlignment {
  Top,
  Left,
  Right,
  Bottom
}

fun PlayerDisplayAlignment.toAlignment(): Alignment {
  return when (this) {
    PlayerDisplayAlignment.Top -> Alignment.TopCenter
    PlayerDisplayAlignment.Left -> Alignment.CenterStart
    PlayerDisplayAlignment.Right -> Alignment.CenterEnd
    PlayerDisplayAlignment.Bottom -> Alignment.BottomCenter
  }
}

fun PlayerDisplayAlignment.isVertical() =
    when (this) {
      PlayerDisplayAlignment.Top -> false
      PlayerDisplayAlignment.Left -> true
      PlayerDisplayAlignment.Right -> true
      PlayerDisplayAlignment.Bottom -> false
    }

@Composable
fun PlayerDisplay(
    name: String,
    balance: Int,
    cards: List<Card>,
    isCurrent: Boolean,
    alignment: PlayerDisplayAlignment,
    selectable: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val cardsScrollState = rememberScrollState()
  val cardsMaxFraction = 0.7F
  Box(
      modifier =
          modifier.clickable(
              selectable,
              onClick = onSelect,
          ),
  ) {
    when (alignment) {
      PlayerDisplayAlignment.Bottom -> {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Text(
              "$name ($balance)",
              fontSize = 24.sp,
              textDecoration = if (isCurrent) TextDecoration.Underline else TextDecoration.None)
          Spacer(Modifier.height(8.dp))
          Row(
              horizontalArrangement = Arrangement.Center,
              modifier =
                  Modifier.fillMaxWidth(cardsMaxFraction).horizontalScroll(cardsScrollState)) {
                cards.forEach { CardImage(it) }
              }
        }
      }

      PlayerDisplayAlignment.Top -> {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Row(
              horizontalArrangement = Arrangement.Center,
              modifier =
                  Modifier.fillMaxWidth(cardsMaxFraction).horizontalScroll(cardsScrollState)) {
                cards.forEach { CardImage(it) }
              }
          Spacer(Modifier.height(8.dp))
          Text(
              "$name ($balance)",
              fontSize = 24.sp,
              textDecoration = if (isCurrent) TextDecoration.Underline else TextDecoration.None)
        }
      }

      PlayerDisplayAlignment.Left -> {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(
              verticalArrangement = Arrangement.Center,
              modifier =
                  Modifier.fillMaxHeight(cardsMaxFraction).verticalScroll(cardsScrollState)) {
                cards.forEach { CardImage(it) }
              }
          Spacer(Modifier.width(8.dp))
          Text(
              "$name ($balance)",
              modifier = Modifier.rotate(90F),
              fontSize = 24.sp,
              textDecoration = if (isCurrent) TextDecoration.Underline else TextDecoration.None)
        }
      }

      PlayerDisplayAlignment.Right -> {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
              "$name ($balance)",
              modifier = Modifier.rotate(90F),
              fontSize = 24.sp,
              textDecoration = if (isCurrent) TextDecoration.Underline else TextDecoration.None)
          Spacer(Modifier.width(8.dp))
          Column(
              verticalArrangement = Arrangement.Center,
              modifier =
                  Modifier.fillMaxHeight(cardsMaxFraction).verticalScroll(cardsScrollState)) {
                cards.forEach { CardImage(it) }
              }
        }
      }
    }
  }
}

@Preview
@Composable
fun PlayerDisplayPreview() {
  Column {
    PlayerDisplay(
        name = "Player",
        balance = 12,
        cards =
            listOf(
                EntertainmentParkCard(),
                BusinessCentreCard(),
                TVTowerCard(),
                TVCentreCard(),
            ),
        alignment = PlayerDisplayAlignment.Top,
        isCurrent = true,
        onSelect = {},
        selectable = false,
    )
  }
}
