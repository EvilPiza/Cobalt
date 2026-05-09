package org.cobalt.util

import org.cobalt.Cobalt.minecraft
import org.cobalt.mixin.client.MinecraftAccessor
import org.cobalt.util.WindowUtils.scaledMouseX
import org.cobalt.util.WindowUtils.scaledMouseY

object MouseUtils {

  private var mode: MouseMode = MouseMode.NORMAL

  @JvmStatic
  fun isHoveringOver(x: Float, y: Float, width: Float, height: Float): Boolean {
    return scaledMouseX >= x &&
      scaledMouseX <= x + width &&
      scaledMouseY >= y &&
      scaledMouseY <= y + height
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
    (minecraft as MinecraftAccessor).leftClick()
  }

  @JvmStatic
  fun rightClick() {
    (minecraft as MinecraftAccessor).rightClick()
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
