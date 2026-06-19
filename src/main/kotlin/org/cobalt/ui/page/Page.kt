package org.cobalt.ui.page

import org.cobalt.ui.UIComponent
import org.cobalt.ui.component.SidebarComponent
import org.cobalt.ui.component.TopbarComponent
import org.cobalt.util.skia.Skia
import org.cobalt.util.skia.helper.SkiaCorner

internal abstract class Page : UIComponent() {

  abstract val title: String

  override val width: Float
    get() = TopbarComponent.width

  override val height: Float
    get() = SidebarComponent.height - TopbarComponent.height

  override fun renderComponent() {
    Skia.roundedRect(
      xPos, yPos,
      width, height,
      CORNER_RADIUS,
      theme.backgroundPrimary,
      arrayOf(SkiaCorner.BOTTOM_RIGHT)
    )
  }

  companion object {
    protected const val CORNER_RADIUS = 10f
  }

}
