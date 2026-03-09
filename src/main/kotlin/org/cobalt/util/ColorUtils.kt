package org.cobalt.util

import kotlin.math.roundToInt
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor

object ColorUtils {

  @JvmStatic
  fun buildTextGradient(text: String, startColor: Int, endColor: Int): MutableComponent {
    val result = Component.empty()
    val textLength = text.length

    if (textLength <= 1) {
      return Component.literal(text)
        .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(startColor)))
    }

    for (index in text.indices) {
      val ratio = index.toDouble() / (textLength - 1)
      val red = (startColor.red + ratio * (endColor.red - startColor.red)).roundToInt()
      val green = (startColor.green + ratio * (endColor.green - startColor.green)).roundToInt()
      val blue = (startColor.blue + ratio * (endColor.blue - startColor.blue)).roundToInt()
      val interpolatedColor = (red shl 16) or (green shl 8) or blue

      val coloredChar = Component.literal(text[index].toString())
        .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(interpolatedColor)))

      result.append(coloredChar)
    }

    return result
  }

  inline val Int.red get() = this shr 16 and 0xFF
  inline val Int.green get() = this shr 8 and 0xFF
  inline val Int.blue get() = this and 0xFF
  inline val Int.alpha get() = this shr 24 and 0xFF

}
