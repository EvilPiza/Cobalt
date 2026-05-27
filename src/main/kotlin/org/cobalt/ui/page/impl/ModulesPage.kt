package org.cobalt.ui.page.impl

import org.cobalt.ui.UIComponent
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaSide

internal object ModulesPage : UIComponent() {

  override val width: Float
    get() = BODY_WIDTH

  override val height: Float
    get() = 600f

  override fun renderComponent() {
    SkiaShapes.drawHalfRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(BODY_WIDTH, height),
      radius = CORNER_RADIUS,
      color = theme.backgroundPrimary.rgb,
      side = SkiaSide.RIGHT
    )
  }

  private const val BODY_WIDTH = 700f
  private const val CORNER_RADIUS: Float = 10f

}
