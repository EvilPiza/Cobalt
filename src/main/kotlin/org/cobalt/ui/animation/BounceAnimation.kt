package org.cobalt.ui.animation

import kotlin.math.pow

class BounceAnimation(duration: Long) : Animation<Float>(duration) {

  override fun get(start: Float, end: Float, reverse: Boolean): Float {
    if (!isAnimating()) {
      return if (reverse) start else end
    }

    val t = ease()

    return if (reverse) {
      end + (start - end) * t
    } else {
      start + (end - start) * t
    }
  }

  private fun ease(): Float {
    val x = getPercent() / 100f

    return when {
      x < 0.34f -> {
        val t = x / 0.34f
        val easeOut = 1f - (1f - t).pow(2.3f)
        easeOut * 1.12f
      }

      else -> {
        val t = (x - 0.34f) / 0.66f
        val easeOut = 1f - (1f - t).pow(2.0f)
        1.12f - (0.12f * easeOut)
      }
    }
  }
}
