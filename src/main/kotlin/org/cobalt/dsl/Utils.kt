package org.cobalt.dsl

import org.cobalt.Cobalt.minecraft

/**
 * Extracts the red component from an ARGB color integer.
 */
inline val Int.red
  get() = this shr 16 and 0xFF

/**
 * Extracts the green component from an ARGB color integer.
 */
inline val Int.green
  get() = this shr 8 and 0xFF

/**
 * Extracts the blue component from an ARGB color integer.
 */
inline val Int.blue
  get() = this and 0xFF

/**
 * Extracts the alpha component from an ARGB color integer.
 */
inline val Int.alpha
  get() = this shr 24 and 0xFF

/**
 * The current X position of the mouse cursor.
 */
inline val mouseX: Float
  get() = minecraft.mouseHandler.xpos().toFloat()

/**
 * The current Y position of the mouse cursor.
 */
inline val mouseY: Float
  get() = minecraft.mouseHandler.ypos().toFloat()
