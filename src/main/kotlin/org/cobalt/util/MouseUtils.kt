package org.cobalt.util

import org.cobalt.dsl.mouseX
import org.cobalt.dsl.mouseY

object MouseUtils {

  @JvmStatic
  fun getMouseX(): Float {
    return mouseX
  }

  @JvmStatic
  fun getMouseY(): Float {
    return mouseY
  }

}
