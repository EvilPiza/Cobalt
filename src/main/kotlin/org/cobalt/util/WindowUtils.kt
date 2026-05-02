package org.cobalt.util

import kotlin.math.min
import org.cobalt.Cobalt.minecraft

object WindowUtils {

  private const val BASE_WIDTH = 1920f
  private const val BASE_HEIGHT = 1080f

  @JvmStatic
  val windowScale: Float
    get() {
      val windowWidth = minecraft.window.width.toFloat()
      val windowHeight = minecraft.window.height.toFloat()

      return min(windowWidth / BASE_WIDTH, windowHeight / BASE_HEIGHT)
    }

  @JvmStatic
  val scaledWidth: Float
    get() = minecraft.window.width.toFloat() / windowScale

  @JvmStatic
  val scaledHeight: Float
    get() = minecraft.window.height.toFloat() / windowScale

  @JvmStatic
  val scaledMouseX: Float
    get() = minecraft.mouseHandler.xpos().toFloat() / windowScale

  @JvmStatic
  val scaledMouseY: Float
    get() = minecraft.mouseHandler.ypos().toFloat() / windowScale

}
