package org.cobalt.module

import net.minecraft.ChatFormatting
import org.cobalt.ui.theme.Theme
import org.cobalt.ui.theme.ThemeManager
import org.cobalt.util.ChatUtils

abstract class Module(
  val name: String,
  val category: ModuleCategory,
) {

  private var enabled: Boolean = false

  open fun onRegistration() {}

  fun setEnabled(enabled: Boolean) {
    this.enabled = enabled
  }

  fun isEnabled(): Boolean = enabled

}

abstract class Script(
  name: String,
  category: ModuleCategory,
) : Module(name, category) {

  private var running: Boolean = false

  abstract fun onEnable()
  abstract fun onDisable()

  fun startScript() {
    onEnable()
    ChatUtils.sendSystemMessage("$name ${ChatFormatting.GREEN}Started")
    running = true
  }

  fun stopScript() {
    onDisable()
    ChatUtils.sendSystemMessage("$name ${ChatFormatting.RED}Stopped")
    running = false
  }

  fun toggleScript() {
    if (running) {
      stopScript()
    } else {
      startScript()
    }
  }

  fun isRunning(): Boolean = running

}

abstract class RenderableModule(
  name: String,
  category: ModuleCategory,
) : Module(name, category) {

  val theme: Theme
    get() = ThemeManager.getActiveTheme()

  var xPos: Float = 5.0f
  var yPos: Float = 5.0f
  var scale: Float = 1.0f

  abstract fun getWidth(): Float
  abstract fun getHeight(): Float
  abstract fun renderComponent()

}
