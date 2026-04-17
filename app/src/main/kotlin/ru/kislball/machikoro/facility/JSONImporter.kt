package ru.kislball.machikoro.facility

import ru.kislball.machikoro.cards.CardFactory
import ru.kislball.machikoro.cards.CardKind
import ru.kislball.machikoro.game.Game
import ru.kislball.machikoro.game.Player

class JSONImporter : GameImporter {
  override fun import(content: String): Game {
    val playersBlock = parsePlayersBlock(content.trim())
    val players = splitTopLevel(playersBlock).map(::parsePlayer)
    return Game(players)
  }

  private fun parsePlayersBlock(content: String): String {
    require(content.startsWith("{\"players\":[")) { "Invalid JSON: missing players array" }
    require(content.endsWith("]}")) { "Invalid JSON: malformed root object" }
    return content.removePrefix("{\"players\":[").removeSuffix("]}")
  }

  private fun parsePlayer(rawPlayer: String): Player {
    val playerRegex = Regex("""\{"name":"((?:\\.|[^"\\])*)","balance":(-?\d+),"cards":\[(.*)]}""")
    val match =
        playerRegex.matchEntire(rawPlayer.trim())
            ?: throw IllegalArgumentException("Invalid player object: $rawPlayer")

    val name = match.groupValues[1].jsonUnescape()
    val balance = match.groupValues[2].toInt()
    val rawCards = match.groupValues[3]

    val player = Player(name)
    player.balance = balance
    if (rawCards.isNotBlank()) {
      val cards =
          splitTopLevel(rawCards).map { rawCard ->
            val cardText = rawCard.trim()
            require(cardText.startsWith("\"") && cardText.endsWith("\"")) {
              "Invalid card value: $cardText"
            }
            val kindName = cardText.substring(1, cardText.length - 1).jsonUnescape()
            CardFactory.create(CardKind.valueOf(kindName))
          }
      player.cards.addAll(cards)
    }
    return player
  }
}

private fun splitTopLevel(raw: String): List<String> {
  if (raw.isBlank()) return emptyList()

  val result = mutableListOf<String>()
  val current = StringBuilder()
  var curlyDepth = 0
  var squareDepth = 0
  var inString = false
  var escaped = false

  for (ch in raw) {
    if (escaped) {
      current.append(ch)
      escaped = false
      continue
    }

    if (inString) {
      current.append(ch)
      when (ch) {
        '\\' -> escaped = true
        '"' -> inString = false
      }
      continue
    }

    when (ch) {
      '"' -> {
        inString = true
        current.append(ch)
      }
      '{' -> {
        curlyDepth++
        current.append(ch)
      }
      '}' -> {
        curlyDepth--
        current.append(ch)
      }
      '[' -> {
        squareDepth++
        current.append(ch)
      }
      ']' -> {
        squareDepth--
        current.append(ch)
      }
      ',' -> {
        if (curlyDepth == 0 && squareDepth == 0) {
          result.add(current.toString())
          current.clear()
        } else {
          current.append(ch)
        }
      }
      else -> current.append(ch)
    }
  }

  if (current.isNotEmpty()) {
    result.add(current.toString())
  }

  return result.map(String::trim).filter(String::isNotEmpty)
}

private fun String.jsonUnescape(): String {
  val input = this
  val out = StringBuilder()
  var i = 0
  while (i < input.length) {
    val ch = input[i]
    if (ch != '\\') {
      out.append(ch)
      i++
      continue
    }

    require(i + 1 < input.length) { "Invalid JSON string escape sequence" }
    when (val escaped = input[i + 1]) {
      '\\' -> out.append('\\')
      '"' -> out.append('"')
      'b' -> out.append('\b')
      'n' -> out.append('\n')
      'r' -> out.append('\r')
      't' -> out.append('\t')
      'u' -> {
        require(i + 5 < input.length) { "Invalid JSON unicode escape sequence" }
        val code = input.substring(i + 2, i + 6).toInt(16)
        out.append(code.toChar())
        i += 4
      }
      else -> throw IllegalArgumentException("Invalid JSON escape character: $escaped")
    }
    i += 2
  }
  return out.toString()
}
