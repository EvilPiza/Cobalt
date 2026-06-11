package org.cobalt.util

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
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

  @JvmStatic
  fun leftClick() {
    val key = minecraft.options.keyAttack.key
    KeyMapping.click(key)
  }

  @JvmStatic
  fun rightClick() {
    val key = minecraft.options.keyUse.key
    KeyMapping.click(key)
  }

}

enum class MouseMode {
  DEFAULT,
  UNGRAB_MOUSE,
  LOCK_MOUSE
}
