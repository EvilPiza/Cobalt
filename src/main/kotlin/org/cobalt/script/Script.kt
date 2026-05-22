package org.cobalt.script

import net.minecraft.ChatFormatting
import org.cobalt.util.ChatUtils

abstract class Script(
  val name: String,
  val category: ScriptCategory,
) {

  var running: Boolean = false
    private set

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

}
