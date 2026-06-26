package org.cobalt.util

import org.cobalt.Cobalt.minecraft

object MouseUtils {

  @JvmStatic
  var mouseMode: MouseMode = MouseMode.DEFAULT

  @JvmStatic
  val mouseX: Float
    get() = minecraft.mouseHandler.xpos().toFloat()

  @JvmStatic
  val mouseY: Float
    get() = minecraft.mouseHandler.ypos().toFloat()

  @JvmStatic
  fun isHoveringOver(x: Float, y: Float, width: Float, height: Float): Boolean {
    return mouseX >= x &&
      mouseX <= x + width &&
      mouseY >= y &&
      mouseY <= y + height
  }

}

enum class MouseButton {
  LEFT, RIGHT, MIDDLE
}

enum class MouseAction {
  PRESS, RELEASE
}

enum class MouseMode {
  DEFAULT, UNGRAB_MOUSE, LOCK_MOUSE
}
