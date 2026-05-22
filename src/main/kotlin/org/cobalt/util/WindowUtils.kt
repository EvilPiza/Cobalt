package org.cobalt.util

import com.mojang.blaze3d.platform.InputConstants
import org.cobalt.Cobalt.minecraft
import org.lwjgl.glfw.GLFW

object WindowUtils {

  val windowWidth: Float
    get() = minecraft.window.width.toFloat()

  val windowHeight: Float
    get() = minecraft.window.height.toFloat()

  fun isKeyDown(key: InputConstants.Key): Boolean {
    val window = minecraft.window

    return if (key.value > 7) {
      InputConstants.isKeyDown(window, key.value)
    } else {
      GLFW.glfwGetMouseButton(window.handle(), key.value) == GLFW.GLFW_PRESS
    }
  }

}
