package org.cobalt.ui.animation

/**
 * Quadratic ease-out animation.
 */
class EaseOutAnimation(duration: Long) : Animation<Float>(duration) {

  override fun get(start: Float, end: Float, reverse: Boolean): Float {
    val startVal = if (reverse) end else start
    val endVal = if (reverse) start else end
    if (!isAnimating()) return endVal
    return startVal + (endVal - startVal) * easeOutQuad()
  }

  private fun easeOutQuad(): Float {
    val percent = getPercent() / FULL_PERCENT
    return 1 - (1 - percent) * (1 - percent)
  }

  companion object {
    private const val FULL_PERCENT: Float = 100f
  }

}
