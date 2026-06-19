package org.cobalt.module.type

import net.minecraft.ChatFormatting
import org.cobalt.module.Module
import org.cobalt.module.ModuleCategory
import org.cobalt.util.ChatUtils

abstract class Script(
  name: String,
  category: ModuleCategory,
) : Module(name, category) {

  override val identifier: String = name.replace(" ", "")
  override val directoryPath: String = "scripts"

  fun startScript() {
    if (enabled) {
      return
    }

    onEnable()
    ChatUtils.sendSystemMessage("$name Script is now ${ChatFormatting.GREEN}Enabled")
    enabled = true
  }

  fun stopScript() {
    if (!enabled) {
      return
    }

    onDisable()
    ChatUtils.sendSystemMessage("$name Script is now ${ChatFormatting.RED}Disabled")
    enabled = false
  }

}
