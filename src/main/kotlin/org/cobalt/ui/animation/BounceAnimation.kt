package org.cobalt.ui.animation

import kotlin.math.pow

/**
 * Bounce-style animation that applies an easing function with overshoot.
 */
class BounceAnimation(duration: Long) : Animation<Float>(duration) {

  companion object {
    private const val PERCENT_DIVISOR: Float = 100f
    private const val FIRST_PHASE_THRESHOLD: Float = 0.3f
    private const val SECOND_PHASE_RANGE: Float = 0.7f
    private const val OVERSHOOT: Float = 1.05f
    private const val OVERSHOOT_DECAY: Float = 0.05f
    private const val FIRST_EASE_EXP: Float = 3f
    private const val SECOND_EASE_EXP: Float = 2f
  }

  override fun get(start: Float, end: Float, reverse: Boolean): Float {
    if (!isAnimating()) return if (reverse) start else end
    return if (reverse) end + (start - end) * ease() else start + (end - start) * ease()
  }

  private fun ease(): Float {
    val x = getPercent() / PERCENT_DIVISOR

    return when {
      x < FIRST_PHASE_THRESHOLD -> {
        val t = x / FIRST_PHASE_THRESHOLD
        val easeOut = 1f - (1f - t).pow(FIRST_EASE_EXP)
        easeOut * OVERSHOOT
      }

      else -> {
        val t = (x - FIRST_PHASE_THRESHOLD) / SECOND_PHASE_RANGE
        val easeOut = 1f - (1f - t).pow(SECOND_EASE_EXP)
        OVERSHOOT - (OVERSHOOT_DECAY * easeOut)
      }
    }
  }

}

