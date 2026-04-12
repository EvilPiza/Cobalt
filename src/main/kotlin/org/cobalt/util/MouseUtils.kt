package org.cobalt.util

import org.cobalt.dsl.mouseX
import org.cobalt.dsl.mouseY

/** Convenience accessors for the current mouse position in screen coordinates. */
object MouseUtils {

  /** Return the current mouse X position as a Float. */
  @JvmStatic
  fun getMouseX(): Float {
    return mouseX
  }

  /** Return the current mouse Y position as a Float. */
  @JvmStatic
  fun getMouseY(): Float {
    return mouseY
  }

}
