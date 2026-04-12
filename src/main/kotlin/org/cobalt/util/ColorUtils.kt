package org.cobalt.util

import kotlin.math.roundToInt
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import org.cobalt.dsl.alpha
import org.cobalt.dsl.blue
import org.cobalt.dsl.green
import org.cobalt.dsl.red

/** Color-related utility helpers for building text gradients and extracting ARGB components. */
object ColorUtils {

  /** Build a MutableComponent where each character of the input text is colored with an interpolated
   * color between startColor and endColor.
   *
   * @param text the text to colorize
   * @param startColor ARGB integer color used at the start of the text
   * @param endColor ARGB integer color used at the end of the text
   * @return a MutableComponent with per-character gradient coloring
   */
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

  /** Return the red component (0-255) of the supplied ARGB color integer. */
  @JvmStatic
  fun getRed(color: Int) = color.red

  /** Return the green component (0-255) of the supplied ARGB color integer. */
  @JvmStatic
  fun getGreen(color: Int) = color.green

  /** Return the blue component (0-255) of the supplied ARGB color integer. */
  @JvmStatic
  fun getBlue(color: Int) = color.blue

  /** Return the alpha component (0-255) of the supplied ARGB color integer. */
  @JvmStatic
  fun getAlpha(color: Int) = color.alpha

}
