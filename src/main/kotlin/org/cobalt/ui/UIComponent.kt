package org.cobalt.ui

import org.cobalt.ui.theme.Theme
import org.cobalt.ui.theme.ThemeManager

abstract class UIComponent(
  var xPos: Float = 0f,
  var yPos: Float = 0f,
  open val width: Float = 0.0f,
  open val height: Float = 0.0f,
) {

  protected val theme: Theme
    get() = ThemeManager.getActiveTheme()

  abstract fun renderComponent()

  fun updateBounds(xPos: Float, yPos: Float): UIComponent {
    this.xPos = xPos
    this.yPos = yPos
    return this
  }

}
