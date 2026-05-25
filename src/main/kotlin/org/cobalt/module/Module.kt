package org.cobalt.module

import org.cobalt.ui.theme.Theme
import org.cobalt.ui.theme.ThemeManager
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.WindowUtils.windowHeight
import org.cobalt.util.WindowUtils.windowWidth
import org.cobalt.util.setting.Setting
import org.cobalt.util.setting.SettingsContainer

abstract class Module(
  val name: String,
  val category: ModuleCategory,
) : SettingsContainer {

  private val settingsList = mutableListOf<Setting<*>>()

  var enabled: Boolean = false

  override val identifier: String = name.replace(" ", "")
  override val directoryPath: String = "modules"

  open fun onRegistration() {}

  override fun addSettings(vararg settings: Setting<*>) {
    settingsList += settings
  }

  override fun getSettings(): List<Setting<*>> {
    return settingsList
  }

}

abstract class RenderableModule(
  name: String,
  category: ModuleCategory,
  val anchor: Anchor = Anchor.TOP_LEFT,
  var offsetX: Float = 0.01f,
  var offsetY: Float = 0.01f,
  var scale: Float = 1.0f,
) : Module(name, category) {

  val theme: Theme
    get() = ThemeManager.activeTheme

  val screenPosition: Vec2f
    get() = anchor.computePosition(
      offsetX,
      offsetY,
      getWidth() * scale,
      getHeight() * scale,
      windowWidth,
      windowHeight
    )

  val dimensions: Dimensions
    get() = Dimensions(getWidth(), getHeight())

  val xPos: Float
    get() = screenPosition.x

  val yPos: Float
    get() = screenPosition.y

  abstract fun getWidth(): Float
  abstract fun getHeight(): Float
  abstract fun renderComponent()

  enum class Anchor {

    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    CENTER_LEFT,
    CENTER,
    CENTER_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT;

    fun computePosition(
      offsetX: Float,
      offsetY: Float,
      moduleWidth: Float,
      moduleHeight: Float,
      screenWidth: Float,
      screenHeight: Float,
    ): Vec2f {
      val absX = offsetX * screenWidth
      val absY = offsetY * screenHeight

      val x = when (this) {
        TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> absX
        TOP_CENTER, CENTER, BOTTOM_CENTER -> absX - moduleWidth / 2f
        TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT -> absX - moduleWidth
      }
      val y = when (this) {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT -> absY
        CENTER_LEFT, CENTER, CENTER_RIGHT -> absY - moduleHeight / 2f
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> absY - moduleHeight
      }

      return Vec2f(
        x.coerceIn(0f, (screenWidth - moduleWidth).coerceAtLeast(0f)),
        y.coerceIn(0f, (screenHeight - moduleHeight).coerceAtLeast(0f))
      )
    }

  }

}
