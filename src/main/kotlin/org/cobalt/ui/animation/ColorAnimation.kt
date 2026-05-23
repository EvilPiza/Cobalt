package org.cobalt.ui.animation

import java.awt.Color
import kotlin.math.roundToInt

internal class ColorAnimation(duration: Long) {

  private val animation = EaseOutAnimation(duration)

  fun start() =
    animation.start()

  fun get(start: Color, end: Color, reverse: Boolean): Color {
    return Color(
      interpolateColor(start.red, end.red, reverse),
      interpolateColor(start.green, end.green, reverse),
      interpolateColor(start.blue, end.blue, reverse),
      interpolateColor(start.alpha, end.alpha, reverse),
    )
  }

  fun get(start: Int, end: Int, reverse: Boolean): Int {
    val startColor = Color(start, true)
    val endColor = Color(end, true)

    val red = interpolate(startColor.red, endColor.red, reverse)
    val green = interpolate(startColor.green, endColor.green, reverse)
    val blue = interpolate(startColor.blue, endColor.blue, reverse)
    val alpha = interpolate(startColor.alpha, endColor.alpha, reverse)

    return (alpha shl ALPHA_SHIFT) or (red shl RED_SHIFT) or (green shl GREEN_SHIFT) or blue
  }

  private fun interpolate(start: Int, end: Int, reverse: Boolean): Int {
    return animation
      .get(start.toFloat(), end.toFloat(), reverse)
      .roundToInt()
      .coerceIn(MIN_VALUE, MAX_VALUE)
  }

  private fun interpolateColor(start: Int, end: Int, reverse: Boolean): Float {
    return interpolate(start, end, reverse) / MAX_VALUE_FLOAT
  }

  companion object {
    private const val MIN_VALUE = 0
    private const val MAX_VALUE = 255
    private const val MAX_VALUE_FLOAT = 255f

    private const val ALPHA_SHIFT = 24
    private const val RED_SHIFT = 16
    private const val GREEN_SHIFT = 8
  }

}
