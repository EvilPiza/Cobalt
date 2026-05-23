package org.cobalt.ui.theme

import com.google.gson.Gson
import java.awt.Color
import java.nio.file.Files
import kotlin.io.path.notExists
import org.cobalt.Cobalt.configDir

object ThemeManager {

  private val gson = Gson()

  @JvmStatic
  lateinit var activeTheme: Theme
    private set

  @JvmStatic
  var themes: Map<String, Theme> = emptyMap()
    private set

  @JvmStatic
  internal fun loadThemes() {
    val folder = configDir
      .resolve("themes")
      .apply(Files::createDirectories)

    val defaultPath =
      folder.resolve("default.json")

    if (defaultPath.notExists()) {
      val default = javaClass.getResourceAsStream("/assets/cobalt/theme/default.json")
        ?: error("Default theme resource not found")

      Files.copy(default, defaultPath)
    }

    themes = folder.toFile()
      .walkTopDown()
      .filter { it.isFile && it.extension == "json" }
      .mapNotNull { file ->
        runCatching {
          val raw = gson.fromJson(file.readText(), ThemeJson::class.java)
          raw.name to raw.toTheme()
        }.getOrNull()
      }
      .toMap()

    activeTheme = themes["default"]!!
  }

  private data class ColorJson(
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Int = 255
  ) {

    fun toColor() =
      Color(red, green, blue, alpha)

  }

  private data class ThemeJson(
    val name: String,
    val backgroundPrimary: ColorJson,
    val backgroundSecondary: ColorJson,
    val border: ColorJson,
    val accentPrimary: ColorJson,
    val accentSecondary: ColorJson,
    val textPrimary: ColorJson,
    val textSecondary: ColorJson,
    val textMuted: ColorJson,
    val textDisabled: ColorJson,
    val success: ColorJson,
    val warning: ColorJson,
    val error: ColorJson,
    val info: ColorJson
  ) {

    fun toTheme() = Theme(
      name = name,
      backgroundPrimary = backgroundPrimary.toColor(),
      backgroundSecondary = backgroundSecondary.toColor(),
      border = border.toColor(),
      accentPrimary = accentPrimary.toColor(),
      accentSecondary = accentSecondary.toColor(),
      textPrimary = textPrimary.toColor(),
      textSecondary = textSecondary.toColor(),
      textMuted = textMuted.toColor(),
      textDisabled = textDisabled.toColor(),
      success = success.toColor(),
      warning = warning.toColor(),
      error = error.toColor(),
      info = info.toColor()
    )

  }

}
