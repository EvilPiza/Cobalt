package org.cobalt.util.setting

import org.cobalt.util.config.ConfigManager

interface SettingsContainer {

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
