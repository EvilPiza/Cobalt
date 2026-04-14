/*
 * Original code Copyright (c) 2025 odtheking
 * Licensed under the BSD 3-Clause License
 */

package org.cobalt.ui.animation

/** Generic animation base for interpolating values over a duration in milliseconds. */
abstract class Animation<T>(private val duration: Long) {

  companion object {
    private const val PERCENT_MAX: Float = 100f
    private const val MIN_PROGRESS: Float = 0f
    private const val MAX_PROGRESS: Float = 1f
    private const val ZERO_TIME: Long = 0L
  }

  private var startTime: Long = ZERO_TIME
  private var animating = false
  private var reversed = false

  /** Compute the interpolated value between start and end for the current animation progress.
   *
   * @param start starting value
   * @param end ending value
   * @param reverse whether the animation is reversed
   */
  abstract fun get(start: T, end: T, reverse: Boolean = false): T

  /** Start or toggle the animation; handles reversal when already animating. */
  fun start() {
    val currentTime = System.currentTimeMillis()

    if (!animating) {
      animating = true
      reversed = false
      startTime = currentTime
      return
    }

    val percent = ((currentTime - startTime) / duration.toFloat()).coerceIn(MIN_PROGRESS, MAX_PROGRESS)
    reversed = !reversed
    startTime = currentTime - ((MAX_PROGRESS - percent) * duration).toLong()
    return
  }

  /** Return animation progress as a percentage between 0 and 100. */
  fun getPercent(): Float {
    if (!animating) return PERCENT_MAX
    val percent = ((System.currentTimeMillis() - startTime) / duration.toFloat() * PERCENT_MAX)

    if (percent >= PERCENT_MAX) {
      animating = false
      return PERCENT_MAX
    }

    return percent.coerceAtMost(PERCENT_MAX)
  }

  /** Whether the animation is currently running. */
  fun isAnimating(): Boolean {
    return animating
  }

}
