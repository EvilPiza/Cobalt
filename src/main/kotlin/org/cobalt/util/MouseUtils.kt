package org.cobalt.util

import org.cobalt.Cobalt.minecraft
import org.cobalt.mixin.client.MinecraftAccessor

object MouseUtils {

  private var mode: MouseMode = MouseMode.NORMAL

  /**
   * Returns the current mouse behavior mode.
   *
   * @return the active [MouseMode]
   */
  @JvmStatic
  fun getMouseMode(): MouseMode {
    return mode
  }

  /**
   * Sets the current mouse mode.
   *
   * @param mode the new mouse mode to apply
   */
  @JvmStatic
  fun setMouseMode(mode: MouseMode) {
    this.mode = mode
  }

  /**
   * Simulates a left mouse click in Minecraft.
   */
  @JvmStatic
  fun leftClick() {
    (minecraft as MinecraftAccessor).leftClick()
  }

  /**
   * Simulates a right mouse click in Minecraft.
   */
  @JvmStatic
  fun rightClick() {
    (minecraft as MinecraftAccessor).rightClick()
  }

  /**
   * Checks if FORCE_UNGRAB mode is active (mouse cursor is ungrabbed).
   *
   * @return true if the mouse is forcefully ungrabbed
   */
  @JvmStatic
  fun isForceUngrabbed(): Boolean {
    return mode == MouseMode.FORCE_UNGRAB
  }

  /**
   * Checks if LOCKED mode is active (disables camera rotation).
   *
   * @return true if camera rotation is disabled
   */
  @JvmStatic
  fun shouldBlockRotation(): Boolean {
    return mode == MouseMode.LOCKED
  }

}

/**
 * Represents the current mouse behavior mode used by the client.
 */
enum class MouseMode {

  /**
   * Default Minecraft mouse behavior.
   */
  NORMAL,

  /**
   * Forces the mouse cursor to be released from the game.
   */
  FORCE_UNGRAB,

  /**
   * Keeps the mouse captured by the game but disables camera rotation.
   */
  LOCKED

}
