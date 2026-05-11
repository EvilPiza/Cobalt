package org.cobalt.util

import org.cobalt.Cobalt.minecraft

object WindowUtils {

  val windowWidth: Float
    get() = minecraft.window.width.toFloat()

  val windowHeight: Float
    get() = minecraft.window.height.toFloat()

}
