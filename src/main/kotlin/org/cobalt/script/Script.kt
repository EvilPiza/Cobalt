package org.cobalt.script

import net.minecraft.ChatFormatting
import org.cobalt.util.ChatUtils
import org.cobalt.util.setting.Setting
import org.cobalt.util.setting.SettingsContainer

abstract class Script(
  val name: String,
  val category: ScriptCategory,
) : SettingsContainer {

  private val settingsList = mutableListOf<Setting<*>>()

  override val identifier: String = name.replace(" ", "")
  override val directoryPath: String = "scripts"

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

  override fun addSettings(vararg settings: Setting<*>) {
    settingsList += settings
  }

  override fun getSettings(): List<Setting<*>> {
    return settingsList
  }

}
