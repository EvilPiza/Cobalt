package org.cobalt.util

import net.minecraft.client.KeyMapping
import org.cobalt.Cobalt.minecraft


object KeybindUtils {

  @JvmStatic
  val allKeys = arrayOf(
    minecraft.options.keyAttack,
    minecraft.options.keyUse,
    minecraft.options.keyDown,
    minecraft.options.keyUp,
    minecraft.options.keyLeft,
    minecraft.options.keyRight,
    minecraft.options.keyJump,
    minecraft.options.keyShift,
    minecraft.options.keySprint,
  )

  @JvmStatic
  val movementKeys = arrayOf(
    minecraft.options.keyDown,
    minecraft.options.keyUp,
    minecraft.options.keyLeft,
    minecraft.options.keyRight,
    minecraft.options.keyJump,
    minecraft.options.keyShift,
  )

  @JvmStatic
  fun leftClick() {
    press(minecraft.options.keyAttack)
  }

  @JvmStatic
  fun middleClick() {
    press(minecraft.options.keyPickItem)
  }

  @JvmStatic
  fun rightClick() {
    press(minecraft.options.keyUse)
  }

  @JvmStatic
  fun press(keyMapping: KeyMapping) {
    if (minecraft.gui.screen() != null) {
      return
    }

    KeyMapping.click(keyMapping.key)
  }

  @JvmStatic
  fun setKeyState(keyMapping: KeyMapping, pressed: Boolean) {
    val shouldBePressed = pressed && minecraft.gui.screen() == null

    if (shouldBePressed) {
      if (!keyMapping.isDown) {
        press(keyMapping)
        keyMapping.isDown = true
      }
    } else if (keyMapping.isDown) {
      keyMapping.isDown = false
    }
  }

  @JvmStatic
  fun stopMovement(vararg ignoreKeys: KeyMapping) {
    movementKeys
      .filter { it !in ignoreKeys }
      .forEach {
        setKeyState(it, false)
      }
  }

  @JvmStatic
  fun holdThese(vararg keyMappings: KeyMapping) {
    releaseAllExcept(*keyMappings)

    keyMappings.forEach { key ->
      setKeyState(key, true)
    }
  }

  @JvmStatic
  fun releaseAllExcept(vararg keyMappings: KeyMapping) {
    allKeys.forEach { key ->
      if (key !in keyMappings && key.isDown) {
        setKeyState(key, false)
      }
    }
  }

}
