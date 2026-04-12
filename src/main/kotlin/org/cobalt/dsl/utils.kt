package org.cobalt.dsl

import org.cobalt.Cobalt.minecraft


/** Extract the red component (0-255) from an ARGB integer. */
inline val Int.red
  get() = this shr 16 and 0xFF

/** Extract the green component (0-255) from an ARGB integer. */
inline val Int.green
  get() = this shr 8 and 0xFF

/** Extract the blue component (0-255) from an ARGB integer. */
inline val Int.blue
  get() = this and 0xFF

/** Extract the alpha component (0-255) from an ARGB integer. */
inline val Int.alpha
  get() = this shr 24 and 0xFF

/** Current mouse X position in screen coordinates as a Float. */
inline val mouseX: Float
  get() = minecraft.mouseHandler.xpos().toFloat()

/** Current mouse Y position in screen coordinates as a Float. */
inline val mouseY: Float
  get() = minecraft.mouseHandler.ypos().toFloat()
