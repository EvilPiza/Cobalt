package org.cobalt.util

import org.cobalt.Cobalt.minecraft
import org.cobalt.mixin.client.MinecraftAccessor

object MouseUtils {

  private var mode: MouseMode = MouseMode.NORMAL

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
