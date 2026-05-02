package org.cobalt.ui.page

import org.cobalt.ui.UIComponent
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaShapes

internal object ModulesPage : UIComponent(
  xPos = 0f, yPos = 0f,
  width = 900f,
  height = 600f
) {

  override fun renderComponent() {
    SkiaShapes.drawRect(Vec2f(xPos, yPos), Dimensions(width, height), theme.backgroundPrimary)
  }

}
