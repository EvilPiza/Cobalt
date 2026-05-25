package org.cobalt.ui.page

import org.cobalt.Cobalt.minecraft
import org.cobalt.ui.UIComponent
import org.cobalt.ui.screen.HudEditorScreen
import org.cobalt.util.helper.TickScheduler

enum class Page(
  val label: String,
  val iconPath: String,
  val component: UIComponent?,
  val onClick: () -> Unit = {},
) {
  SCRIPTS(
    label = "Scripts",
    iconPath = "/assets/cobalt/ui/scripts.svg",
    component = ScriptsPage
  ),
  MODULES(
    label = "Modules",
    iconPath = "/assets/cobalt/ui/modules.svg",
    component = ModulesPage
  ),
  THEMES(
    label = "Themes",
    iconPath = "/assets/cobalt/ui/themes.svg",
    component = ThemesPage
  ),
  HUD(
    label = "Edit HUD",
    iconPath = "/assets/cobalt/ui/hud.svg",
    component = null,
    onClick = {
      TickScheduler.schedule(1L) { minecraft.setScreen(HudEditorScreen) }
    }
  )
}
