package org.cobalt.util

import kotlin.math.min
import org.cobalt.Cobalt.minecraft
import org.cobalt.dsl.mouseX
import org.cobalt.dsl.mouseY

object WindowUtils {

  private const val BASE_WIDTH = 1920f
  private const val BASE_HEIGHT = 1080f

  /**
   * Calculates a UI scaling factor based on the current window size.
   *
   * The scale is derived by comparing the current window dimensions to a
   * fixed base resolution (1920x1080) and returning the smaller ratio
   * to maintain aspect consistency.
   *
   * @return scale factor used for UI rendering
   */
  @JvmStatic
  fun getWindowScale(): Float {
    val windowWidth = minecraft.window.width.toFloat()
    val windowHeight = minecraft.window.height.toFloat()

    return min(windowWidth / BASE_WIDTH, windowHeight / BASE_HEIGHT)
  }

  /**
   * Gets the current mouse X position in screen space.
   *
   * @return mouse X coordinate in window space
   */
  @JvmStatic
  fun getMouseX(): Float = mouseX

  /**
   * Gets the current mouse Y position in screen space.
   *
   * @return mouse Y coordinate in window space
   */
  @JvmStatic
  fun getMouseY(): Float = mouseY

}
