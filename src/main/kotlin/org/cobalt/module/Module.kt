package org.cobalt.module

import net.minecraft.ChatFormatting
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

interface Renderable {

  val renderProps: RenderProperties

  var xPos: Float
    get() = renderProps.xPos;
    set(value) { renderProps.xPos = value }

  var yPos: Float
    get() = renderProps.yPos;
    set(value) { renderProps.yPos = value }

  var scale: Float
    get() = renderProps.scale;
    set(value) { renderProps.scale = value }

  fun getWidth(): Float
  fun getHeight(): Float
  fun renderComponent()

}

private const val DEFAULT_OFFSET = 5.0f
private const val DEFAULT_SCALE = 1.0f

data class RenderProperties(
  var xPos: Float = DEFAULT_OFFSET,
  var yPos: Float = DEFAULT_OFFSET,
  var scale: Float = DEFAULT_SCALE,
)
