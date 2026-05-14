package org.cobalt.ui.component

import org.cobalt.ui.UIComponent
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaSide

class SidebarComponent : UIComponent(
  width = 200f,
  height = 600f
) {

  override fun renderComponent() {
    SkiaShapes.drawHalfRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      radius = CORNER_RADIUS,
      color = theme.backgroundSecondary,
      side = SkiaSide.LEFT
    )
  }

  companion object {
    private const val CORNER_RADIUS: Float = 10f
  }

}
