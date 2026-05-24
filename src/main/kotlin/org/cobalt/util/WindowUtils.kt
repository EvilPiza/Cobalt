package org.cobalt.util

import com.mojang.blaze3d.platform.InputConstants
import org.cobalt.Cobalt.minecraft
import org.lwjgl.glfw.GLFW

object WindowUtils {

  @JvmStatic
  val windowWidth: Float
    get() = minecraft.window.width.toFloat()

  @JvmStatic
  val windowHeight: Float
    get() = minecraft.window.height.toFloat()

  @JvmStatic
  fun isKeyDown(key: InputConstants.Key): Boolean {
    val window = minecraft.window

    return if (key.value > GLFW.GLFW_MOUSE_BUTTON_LAST) {
      InputConstants.isKeyDown(window, key.value)
    } else {
      GLFW.glfwGetMouseButton(window.handle(), key.value) == GLFW.GLFW_PRESS
    }
  }

}
