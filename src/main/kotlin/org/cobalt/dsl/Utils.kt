package org.cobalt.dsl

import org.cobalt.Cobalt.minecraft

inline val Int.red
  get() = this shr 16 and 0xFF

inline val Int.green
  get() = this shr 8 and 0xFF

inline val Int.blue
  get() = this and 0xFF

inline val Int.alpha
  get() = this shr 24 and 0xFF

inline val mouseX: Float
  get() = minecraft.mouseHandler.xpos().toFloat()

inline val mouseY: Float
  get() = minecraft.mouseHandler.ypos().toFloat()
