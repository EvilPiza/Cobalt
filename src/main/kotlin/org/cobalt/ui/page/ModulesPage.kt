package org.cobalt.ui.page

import org.cobalt.ui.UIComponent
import org.cobalt.ui.component.SidebarComponent
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaSide

internal object ModulesPage : UIComponent(
  height = 600f
) {

  private val sidebar = SidebarComponent()

  override val width: Float
    get() = BODY_WIDTH + sidebar.width

  init {
    addChild(sidebar)
  }

  override fun renderComponent() {
    SkiaShapes.drawHalfRoundedRect(
      Vec2f(xPos + sidebar.width, yPos),
      Dimensions(BODY_WIDTH, height),
      radius = CORNER_RADIUS,
      color = theme.backgroundPrimary,
      side = SkiaSide.RIGHT
    )

    drawSidebar()
    drawBorders()
  }

  private fun drawSidebar() {
    sidebar
      .updateBounds(xPos, yPos)
      .renderComponent()
  }

  private fun drawBorders() {
    SkiaShapes.drawRoundedOutline(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      radius = CORNER_RADIUS,
      color = theme.border,
      thickness = 2f
    )

    SkiaShapes.drawLine(
      Vec2f(xPos + sidebar.width, yPos),
      Vec2f(xPos + sidebar.width, yPos + height),
      color = theme.border,
      thickness = 2f
    )
  }

  private const val BODY_WIDTH = 700f
  private const val CORNER_RADIUS: Float = 10f

}
