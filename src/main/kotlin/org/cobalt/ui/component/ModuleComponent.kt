package org.cobalt.ui.component

import org.cobalt.module.Module
import org.cobalt.ui.UIComponent
import org.cobalt.util.skia.Skia

class ModuleComponent(
  val module: Module
) : UIComponent(
  width = WIDTH,
  height = BASE_HEIGHT
) {

  override fun renderComponent() {
    Skia.roundedRect(
      xPos, yPos,
      width, height,
      5f, theme.backgroundSecondary
    )

    Skia.roundedOutline(
      xPos, yPos,
      width, height,
      1f, 5f, theme.border
    )

    Skia.text(
      Skia.regularFont, module.name,
      xPos + PADDING, yPos + PADDING,
      FONT_SIZE, theme.textPrimary
    )
  }

  companion object {
    const val WIDTH = 320f

    private const val BASE_HEIGHT = 50f
    private const val FONT_SIZE = 16f
    private const val PADDING = (BASE_HEIGHT - FONT_SIZE) / 2
  }

}
