package org.cobalt.util.config

import org.cobalt.ui.component.setting.Setting

interface SettingContainer {

  val identifier: String
  val directoryPath: String

  fun addSettings(vararg settings: Setting<*>)
  fun getSettings(): List<Setting<*>>

  fun saveConfig() {
    ConfigManager.saveConfig(this)
  }

  fun loadConfig() {
    ConfigManager.loadConfig(this)
  }

}
