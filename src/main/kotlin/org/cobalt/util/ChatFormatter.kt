package org.cobalt.util

import kotlin.math.roundToInt
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor

internal object ChatFormatter {

  private const val FORMAT_CODE = '\u00A7'

  private val colorTags = mapOf(
    "black" to ChatFormatting.BLACK,
    "dark_blue" to ChatFormatting.DARK_BLUE,
    "dark_green" to ChatFormatting.DARK_GREEN,
    "dark_aqua" to ChatFormatting.DARK_AQUA,
    "dark_red" to ChatFormatting.DARK_RED,
    "dark_purple" to ChatFormatting.DARK_PURPLE,
    "gold" to ChatFormatting.GOLD,
    "gray" to ChatFormatting.GRAY,
    "grey" to ChatFormatting.GRAY,
    "dark_gray" to ChatFormatting.DARK_GRAY,
    "dark_grey" to ChatFormatting.DARK_GRAY,
    "blue" to ChatFormatting.BLUE,
    "green" to ChatFormatting.GREEN,
    "aqua" to ChatFormatting.AQUA,
    "red" to ChatFormatting.RED,
    "light_purple" to ChatFormatting.LIGHT_PURPLE,
    "yellow" to ChatFormatting.YELLOW,
    "white" to ChatFormatting.WHITE
  )

  private val styleTags = setOf(
    "bold",
    "b",
    "italic",
    "i",
    "underlined",
    "underline",
    "u",
    "strikethrough",
    "st",
    "obfuscated",
    "obf"
  )

  fun parse(text: String, baseStyle: Style = Style.EMPTY): MutableComponent {
    val result = Component.empty()
    val styleStack = ArrayDeque<Style>()
    val chunk = StringBuilder()
    var style = baseStyle
    var index = 0

    fun flush() {
      if (chunk.isEmpty()) {
        return
      }

      result.append(Component.literal(chunk.toString()).setStyle(style))
      chunk.clear()
    }

    while (index < text.length) {
      if (text[index] == '<') {
        val tagEnd = text.indexOf('>', index + 1)

        if (tagEnd != -1) {
          val tag = text.substring(index + 1, tagEnd)
          val gradient = parseGradientTag(tag)

          if (gradient != null) {
            val closeStart = text.indexOf("</gradient>", tagEnd + 1, ignoreCase = true)

            if (closeStart != -1) {
              flush()
              result.append(
                gradientComponent(
                  text.substring(tagEnd + 1, closeStart),
                  style,
                  gradient.first,
                  gradient.second
                )
              )
              index = closeStart + "</gradient>".length
              continue
            }
          }

          val nextStyle = applyTag(tag, style, styleStack, baseStyle)

          if (nextStyle != null) {
            flush()
            style = nextStyle
            index = tagEnd + 1
            continue
          }
        }
      }

      if (text[index] == FORMAT_CODE && index + 1 < text.length) {
        val hexColor = parseLegacyHex(text, index)

        if (hexColor != null) {
          flush()
          style = baseStyle.withColor(TextColor.fromRgb(hexColor))
          index += 14
          continue
        }

        val formatting = ChatFormatting.getByCode(text[index + 1].lowercaseChar())

        if (formatting != null) {
          flush()
          style = if (formatting == ChatFormatting.RESET) {
            styleStack.clear()
            baseStyle
          } else {
            style.applyLegacyFormat(formatting)
          }
          index += 2
          continue
        }
      }

      chunk.append(text[index])
      index++
    }

    flush()
    return result
  }

  private fun applyTag(
    rawTag: String,
    currentStyle: Style,
    styleStack: ArrayDeque<Style>,
    baseStyle: Style,
  ): Style? {
    val tag = rawTag.trim().lowercase()

    if (tag.startsWith("/")) {
      val tagName = tag.drop(1)

      return if ((tagName in colorTags || tagName in styleTags) && styleStack.isNotEmpty()) {
        styleStack.removeLast()
      } else {
        null
      }
    }

    if (tag == "reset") {
      styleStack.clear()
      return baseStyle
    }

    colorTags[tag]?.let { color ->
      styleStack.addLast(currentStyle)
      return currentStyle.withColor(color)
    }

    parseHexColorTag(tag)?.let { color ->
      styleStack.addLast(currentStyle)
      return currentStyle.withColor(TextColor.fromRgb(color))
    }

    val nextStyle = when (tag) {
      "bold", "b" -> currentStyle.withBold(true)
      "italic", "i" -> currentStyle.withItalic(true)
      "underlined", "underline", "u" -> currentStyle.withUnderlined(true)
      "strikethrough", "st" -> currentStyle.withStrikethrough(true)
      "obfuscated", "obf" -> currentStyle.withObfuscated(true)
      else -> null
    } ?: return null

    styleStack.addLast(currentStyle)
    return nextStyle
  }

  private fun parseGradientTag(tag: String): Pair<Int, Int>? {
    val parts = tag.trim().split(':')

    if (parts.size < 3 || !parts[0].equals("gradient", ignoreCase = true)) {
      return null
    }

    val startColor = parseHexColor(parts[1]) ?: return null
    val endColor = parseHexColor(parts[2]) ?: return null

    return startColor to endColor
  }

  private fun gradientComponent(text: String, style: Style, startColor: Int, endColor: Int): MutableComponent {
    val result = Component.empty()

    if (text.isEmpty()) {
      return result
    }

    if (text.length == 1) {
      return Component.literal(text).setStyle(style.withColor(TextColor.fromRgb(startColor)))
    }

    text.forEachIndexed { index, char ->
      val ratio = index.toDouble() / (text.length - 1).toDouble()
      val red = (startColor.red + ratio * (endColor.red - startColor.red)).roundToInt()
      val green = (startColor.green + ratio * (endColor.green - startColor.green)).roundToInt()
      val blue = (startColor.blue + ratio * (endColor.blue - startColor.blue)).roundToInt()
      val color = (red shl 16) or (green shl 8) or blue

      result.append(Component.literal(char.toString()).setStyle(style.withColor(TextColor.fromRgb(color))))
    }

    return result
  }

  private fun parseHexColorTag(tag: String): Int? {
    return when {
      tag.startsWith("#") -> parseHexColor(tag)
      tag.startsWith("color:#") -> parseHexColor(tag.removePrefix("color:"))
      tag.startsWith("color:") -> parseHexColor(tag.removePrefix("color:"))
      else -> null
    }
  }

  private fun parseHexColor(value: String): Int? {
    val hex = value.removePrefix("#")

    if (hex.length != 6 || hex.any { !it.isHexDigit() }) {
      return null
    }

    return hex.toInt(16)
  }

  private fun parseLegacyHex(text: String, startIndex: Int): Int? {
    if (startIndex + 13 >= text.length || text[startIndex + 1].lowercaseChar() != 'x') {
      return null
    }

    val hex = StringBuilder(6)
    var index = startIndex + 2

    repeat(6) {
      if (text[index] != FORMAT_CODE || !text[index + 1].isHexDigit()) {
        return null
      }

      hex.append(text[index + 1])
      index += 2
    }

    return hex.toString().toInt(16)
  }

  private val Int.red: Int
    get() = (this shr 16) and 0xFF

  private val Int.green: Int
    get() = (this shr 8) and 0xFF

  private val Int.blue: Int
    get() = this and 0xFF

  private fun Char.isHexDigit(): Boolean {
    return this in '0'..'9' || this in 'a'..'f' || this in 'A'..'F'
  }

}
