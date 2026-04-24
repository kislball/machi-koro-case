@file:Suppress("FunctionNaming")

package ru.kislball.machikoro.gui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Dot(color: Color, modifier: Modifier = Modifier) {
  Box(modifier = modifier.size(4.dp).background(color, shape = CircleShape))
}

@Composable
fun DiceGraphic(
    number: Int,
    color: Color = Color.Black,
    modifier: Modifier = Modifier,
) {
  require(number in 1..6) { "Number must be between 1 and 6" }
  Box(
      modifier =
          modifier.size(32.dp).border(2.dp, color, shape = RoundedCornerShape(8.dp)).padding(6.dp),
  ) {
    when (number) {
      1 -> Dot(color, modifier = Modifier.align(Alignment.Center))
      2 -> {
        Dot(color, modifier = Modifier.align(Alignment.TopStart))
        Dot(color, modifier = Modifier.align(Alignment.BottomEnd))
      }
      3 -> {
        Dot(color, modifier = Modifier.align(Alignment.TopStart))
        Dot(color, modifier = Modifier.align(Alignment.Center))
        Dot(color, modifier = Modifier.align(Alignment.BottomEnd))
      }
      4 -> {
        Dot(color, modifier = Modifier.align(Alignment.TopStart))
        Dot(color, modifier = Modifier.align(Alignment.TopEnd))
        Dot(color, modifier = Modifier.align(Alignment.BottomEnd))
        Dot(color, modifier = Modifier.align(Alignment.BottomStart))
      }
      5 -> {
        Dot(color, modifier = Modifier.align(Alignment.TopStart))
        Dot(color, modifier = Modifier.align(Alignment.TopEnd))
        Dot(color, modifier = Modifier.align(Alignment.Center))
        Dot(color, modifier = Modifier.align(Alignment.BottomEnd))
        Dot(color, modifier = Modifier.align(Alignment.BottomStart))
      }
      6 -> {
        Dot(color, modifier = Modifier.align(Alignment.TopStart))
        Dot(color, modifier = Modifier.align(Alignment.TopEnd))
        Dot(color, modifier = Modifier.align(Alignment.CenterStart))
        Dot(color, modifier = Modifier.align(Alignment.CenterEnd))
        Dot(color, modifier = Modifier.align(Alignment.BottomEnd))
        Dot(color, modifier = Modifier.align(Alignment.BottomStart))
      }
    }
  }
}

@Composable
fun DiceRoll(rolls: List<Int>, color: Color = Color.Black) {
  require(rolls.all { it in (1..6) })
  rolls.forEach { DiceGraphic(it, color) }
}

@Preview
@Composable
fun DiceGraphicPreview() {
  Column { DiceRoll((1..6).toList(), color = Color.Black) }
}
