package org.cobalt.util

import kotlin.math.min
import org.cobalt.Cobalt.minecraft
import org.cobalt.dsl.mouseX
import org.cobalt.dsl.mouseY

object WindowUtils {

  private const val BASE_WIDTH = 1920f
  private const val BASE_HEIGHT = 1080f

  @JvmStatic
  fun getWindowScale(): Float {
    val windowWidth = getWidth()
    val windowHeight = getHeight()

    return min(windowWidth / BASE_WIDTH, windowHeight / BASE_HEIGHT)
  }

  @JvmStatic
  fun getWidth(): Float {
    return minecraft.window.width.toFloat()
  }

  @JvmStatic
  fun getHeight(): Float {
    return minecraft.window.height.toFloat()
  }

  @JvmStatic
  fun getMouseX(): Float = mouseX

  @JvmStatic
  fun getMouseY(): Float = mouseY

}
