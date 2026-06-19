package org.cobalt.util.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.bufferedWriter
import kotlin.io.path.exists
import kotlin.io.path.readText
import org.cobalt.Cobalt.configDir
import org.cobalt.module.Module
import org.cobalt.module.type.RenderableModule
import org.cobalt.module.type.Script
import org.cobalt.util.setting.SettingsContainer
import org.cobalt.util.setting.impl.ButtonSetting
import org.cobalt.util.setting.impl.InfoSetting
import org.slf4j.LoggerFactory

object ConfigManager {

  private val logger = LoggerFactory.getLogger(ConfigManager::class.java)
  private val gson: Gson = GsonBuilder()
    .setPrettyPrinting()
    .create()

  private fun getConfigFile(container: SettingsContainer): Path =
    configDir
      .resolve(container.directoryPath)
      .apply(Files::createDirectories)
      .resolve("${container.identifier}.json")

  @JvmStatic
  fun loadConfig(container: SettingsContainer) {
    val configFile = getConfigFile(container)

    if (!configFile.exists()) {
      return
    }

    runCatching {
      gson.fromJson(
        configFile.readText(),
        JsonObject::class.java
      )
    }.onSuccess { json ->
      (container as? Module)
        ?.takeIf { module -> module !is Script }
        ?.let { module ->
          json.get("enabled")?.asBoolean?.let {
            module.enabled = it
          }
        }

      (container as? RenderableModule)?.let { renderableModule ->
        json.get("offsetX")?.asFloat?.let {
          renderableModule.xPos = it
        }

        json.get("offsetY")?.asFloat?.let {
          renderableModule.yPos = it
        }

        json.get("scale")?.asFloat?.let {
          renderableModule.scale = it
        }
      }

      container.getSettings()
        .filterNot { it is InfoSetting || it is ButtonSetting }
        .forEach { setting ->
          json.get(setting.name)?.let(setting::read)
        }
    }.onFailure { exception ->
      logger.error(
        "Failed to load config: ${container.directoryPath}/${container.identifier}",
        exception
      )
    }
  }

  @JvmStatic
  fun saveConfig(container: SettingsContainer) {
    val settings = container.getSettings()
      .filterNot { it is InfoSetting || it is ButtonSetting }

    if (settings.isEmpty() && container !is Module) {
      return
    }

    val jsonObject = JsonObject().apply {
      (container as? Module)
        ?.takeIf { module -> module !is Script }
        ?.let {
          addProperty("enabled", it.enabled)
        }

      (container as? RenderableModule)?.let {
        addProperty("offsetX", it.xPos)
        addProperty("offsetY", it.yPos)
        addProperty("scale", it.scale)
      }

      settings.forEach { setting ->
        add(setting.name, setting.write())
      }
    }

    runCatching {
      getConfigFile(container)
        .bufferedWriter()
        .use { it.write(gson.toJson(jsonObject)) }
    }.onFailure { exception ->
      logger.error(
        "Failed to save config: ${container.directoryPath}/${container.identifier}",
        exception
      )
    }
  }

}
