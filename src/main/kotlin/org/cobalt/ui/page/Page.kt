package org.cobalt.ui.page

import org.cobalt.ui.UIComponent
import org.cobalt.ui.component.SidebarComponent
import org.cobalt.ui.component.TopbarComponent
import org.cobalt.ui.page.impl.ModulesPage
import org.cobalt.ui.page.impl.ScriptsPage
import org.cobalt.ui.page.impl.ThemesPage
import org.cobalt.util.skia.Skia
import org.cobalt.util.skia.helper.SkiaCorner

internal abstract class Page(
  val title: String
) : UIComponent() {

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
