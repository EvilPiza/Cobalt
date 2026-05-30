package org.cobalt.ui.page

import org.cobalt.ui.UIComponent
import org.cobalt.ui.component.SidebarComponent
import org.cobalt.ui.component.TopbarComponent
import org.cobalt.ui.page.impl.ModulesPage
import org.cobalt.ui.page.impl.ScriptsPage
import org.cobalt.ui.page.impl.ThemesPage
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaCorner
import org.cobalt.util.skia.SkiaShapes

internal abstract class Page : UIComponent() {

  override val width: Float
    get() = TopbarComponent.width

  override val height: Float
    get() = SidebarComponent.height - TopbarComponent.height

  override fun renderComponent() {
    SkiaShapes.drawRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      CORNER_RADIUS,
      theme.backgroundPrimary.rgb,
      listOf(SkiaCorner.BOTTOM_RIGHT)
    )
  }

  companion object {
    protected const val CORNER_RADIUS = 10f
  }

}

internal enum class PageType(
  val label: String,
  val iconPath: String,
  val page: Page?,
) {
  SCRIPTS(
    label = "Scripts",
    iconPath = "/assets/cobalt/ui/scripts.svg",
    page = ScriptsPage
  ),
  MODULES(
    label = "Modules",
    iconPath = "/assets/cobalt/ui/modules.svg",
    page = ModulesPage
  ),
  THEMES(
    label = "Themes",
    iconPath = "/assets/cobalt/ui/themes.svg",
    page = ThemesPage
  ),
  HUD(
    label = "Edit HUD",
    iconPath = "/assets/cobalt/ui/hud.svg",
    page = null
  )
}
