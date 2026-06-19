package org.cobalt.module

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import org.cobalt.ui.theme.Theme
import org.cobalt.ui.theme.ThemeManager
import org.cobalt.util.ChatUtils
import org.cobalt.util.setting.Setting
import org.cobalt.util.setting.SettingsContainer

abstract class Module(
  val name: String,
  val category: ModuleCategory,
) : SettingsContainer {

  protected val minecraft
    get() = Minecraft.getInstance()

  private val settingsList = mutableListOf<Setting<*>>()
  override val identifier: String = name.replace(" ", "")
  override val directoryPath: String = "modules"

  var enabled: Boolean = true
    set(value) {
      if (field == value) {
        return
      }

      if (value) {
        onEnable()
      } else {
        onDisable()
      }

      field = value
    }

  open fun onRegistration() {}
  open fun onEnable() {}
  open fun onDisable() {}

  override fun addSettings(vararg settings: Setting<*>) {
    settingsList += settings
  }

  override fun getSettings(): List<Setting<*>> {
    return settingsList
  }

}
