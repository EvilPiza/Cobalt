/*
 * Original code Copyright (c) 2025 odtheking
 * Licensed under the BSD 3-Clause License
 */

package org.cobalt.ui.animation

/** Generic animation base for interpolating values over a duration in milliseconds. */
abstract class Animation<T>(private val duration: Long) {

  private var startTime: Long = 0L
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

    val percent = ((currentTime - startTime) / duration.toFloat()).coerceIn(0f, 1f)
    reversed = !reversed
    startTime = currentTime - ((1f - percent) * duration).toLong()
    return
  }

  /** Return animation progress as a percentage between 0 and 100. */
  fun getPercent(): Float {
    if (!animating) return 100f
    val percent = ((System.currentTimeMillis() - startTime) / duration.toFloat() * 100f)

    if (percent >= 100f) {
      animating = false
      return 100f
    }

    return percent.coerceAtMost(100f)
  }

  /** Whether the animation is currently running. */
  fun isAnimating(): Boolean {
    return animating
  }

}
