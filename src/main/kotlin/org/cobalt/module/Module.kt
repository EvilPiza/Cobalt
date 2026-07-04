package org.cobalt.module

import net.minecraft.client.Minecraft
import org.cobalt.event.EventBus
import org.cobalt.ui.component.setting.Setting
import org.cobalt.ui.notification.Notification
import org.cobalt.ui.notification.NotificationManager
import org.cobalt.util.ChatUtils
import org.cobalt.util.config.SettingContainer

abstract class Module(
  val name: String,
  val category: ModuleCategory,
  val toggleable: Boolean = true,
  startValue: Boolean = false,
) : SettingContainer {

  protected val minecraft
    get() = Minecraft.getInstance()

  private val settingsList = mutableListOf<Setting<*>>()
  override val identifier: String = name.replace(" ", "")
  override val directoryPath: String = "modules"

  var enabled: Boolean = startValue
    set(value) {
      if (!toggleable || field == value) {
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

  open fun onEnable() {
    EventBus.register(this)
    ChatUtils.sendSystemMessage("${this.name} has been <green>enabled!</green>")
  }

  open fun onDisable() {
    EventBus.unregister(this)
    ChatUtils.sendSystemMessage("${this.name} has been <red>disabled!</red>")
  }

  override fun addSettings(vararg settings: Setting<*>) {
    settingsList += settings
  }

  override fun getSettings(): List<Setting<*>> {
    return settingsList
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }

    if (other !is Module) {
      return false
    }

    return this.name.replace(" ", "").equals(other.name.replace(" ", ""), ignoreCase = true)
  }

  override fun hashCode(): Int {
    return name.replace(" ", "").lowercase().hashCode()
  }

}
