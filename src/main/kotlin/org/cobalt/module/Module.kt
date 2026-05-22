package org.cobalt.module

import org.cobalt.ui.theme.Theme
import org.cobalt.ui.theme.ThemeManager
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.WindowUtils.windowHeight
import org.cobalt.util.WindowUtils.windowWidth

abstract class Module(
  val name: String,
  val category: ModuleCategory,
) {

  var enabled: Boolean = false

  open fun onRegistration() {}

}

abstract class RenderableModule(
  name: String,
  category: ModuleCategory,
  var anchor: Anchor = Anchor.TOP_LEFT,
  var offsetX: Float = 5.0f,
  var offsetY: Float = 5.0f,
  var scale: Float = 1.0f,
) : Module(name, category) {

  val theme: Theme
    get() = ThemeManager.getActiveTheme()

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
      val x = when (this) {
        TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> offsetX
        TOP_CENTER, CENTER, BOTTOM_CENTER -> screenWidth / 2f - moduleWidth / 2f + offsetX
        TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT -> screenWidth - moduleWidth - offsetX
      }

      val y = when (this) {
        TOP_LEFT, TOP_CENTER, TOP_RIGHT -> offsetY
        CENTER_LEFT, CENTER, CENTER_RIGHT -> screenHeight / 2f - moduleHeight / 2f + offsetY
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> screenHeight - moduleHeight - offsetY
      }

      return Vec2f(
        x.coerceIn(0f, (screenWidth - moduleWidth).coerceAtLeast(0f)),
        y.coerceIn(0f, (screenHeight - moduleHeight).coerceAtLeast(0f))
      )
    }

  }

}
