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

object ColorUtils {

  private const val MIN_TEXT_LENGTH = 1
  private const val SHIFT_RED = 16
  private const val SHIFT_GREEN = 8

  /**
   * Creates a gradient-colored text component where each character
   * is interpolated between two ARGB colors.
   *
   * @param text input text
   * @param startColor starting ARGB color
   * @param endColor ending ARGB color
   * @return gradient-colored [MutableComponent]
   */
  @JvmStatic
  fun buildTextGradient(text: String, startColor: Int, endColor: Int): MutableComponent {
    val result = Component.empty()
    val textLength = text.length

    if (textLength <= MIN_TEXT_LENGTH) {
      return Component.literal(text)
        .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(startColor)))
    }

    for (index in text.indices) {
      val denominator = (textLength - MIN_TEXT_LENGTH).toDouble()
      val ratio = index.toDouble() / denominator
      val red = (startColor.red + ratio * (endColor.red - startColor.red)).roundToInt()
      val green = (startColor.green + ratio * (endColor.green - startColor.green)).roundToInt()
      val blue = (startColor.blue + ratio * (endColor.blue - startColor.blue)).roundToInt()
      val interpolatedColor = (red shl SHIFT_RED) or (green shl SHIFT_GREEN) or blue

      val coloredChar = Component.literal(text[index].toString())
        .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(interpolatedColor)))

      result.append(coloredChar)
    }

    return result
  }

  /**
   * Extracts the red channel from an ARGB color value.
   *
   * @param color the ARGB color integer
   * @return the red component (0–255)
   */
  @JvmStatic
  fun getRed(color: Int) = color.red

  /**
   * Extracts the green channel from an ARGB color value.
   *
   * @param color the ARGB color integer
   * @return the green component (0–255)
   */
  @JvmStatic
  fun getGreen(color: Int) = color.green

  /**
   * Extracts the blue channel from an ARGB color value.
   *
   * @param color the ARGB color integer
   * @return the blue component (0–255)
   */
  @JvmStatic
  fun getBlue(color: Int) = color.blue

  /**
   * Extracts the alpha channel from an ARGB color value.
   *
   * @param color the ARGB color integer
   * @return the alpha component (0–255)
   */
  @JvmStatic
  fun getAlpha(color: Int) = color.alpha

}
