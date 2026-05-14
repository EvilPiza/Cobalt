package org.cobalt.util

import org.cobalt.Cobalt.minecraft

object MouseUtils {

  private var mode: MouseMode = MouseMode.NORMAL

  val mouseX: Float
    get() = minecraft.mouseHandler.xpos().toFloat()

  val mouseY: Float
    get() = minecraft.mouseHandler.ypos().toFloat()

  @JvmStatic
  fun isHoveringOver(x: Float, y: Float, width: Float, height: Float): Boolean {
    return mouseX >= x &&
      mouseX <= x + width &&
      mouseY >= y &&
      mouseY <= y + height
  }

  @JvmStatic
  fun getMouseMode(): MouseMode {
    return mode
  }

  @JvmStatic
  fun setMouseMode(mode: MouseMode) {
    this.mode = mode
  }

  @JvmStatic
  fun leftClick() {
    minecraft.startAttack()
  }

  @JvmStatic
  fun rightClick() {
    minecraft.startUseItem()
  }

  @JvmStatic
  fun isForceUngrabbed(): Boolean {
    return mode == MouseMode.FORCE_UNGRAB
  }

  @JvmStatic
  fun shouldBlockRotation(): Boolean {
    return mode == MouseMode.LOCKED
  }

}

enum class MouseMode {
  NORMAL,
  FORCE_UNGRAB,
  LOCKED
}
