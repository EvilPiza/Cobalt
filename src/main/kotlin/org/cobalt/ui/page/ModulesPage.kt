package org.cobalt.ui.page

import org.cobalt.ui.UIComponent
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaShapes

internal object ModulesPage : UIComponent(
  width = 900f,
  height = 600f
) {

  override fun renderComponent() {
    SkiaShapes.drawRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      radius = CORNER_RADIUS,
      color = theme.backgroundPrimary,
    )
  }

  private const val CORNER_RADIUS: Float = 5f

}
