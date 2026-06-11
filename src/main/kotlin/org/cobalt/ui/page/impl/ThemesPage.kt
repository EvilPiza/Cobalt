package org.cobalt.ui.page.impl

import org.cobalt.ui.component.ThemeComponent
import org.cobalt.ui.page.Page
import org.cobalt.ui.theme.ThemeManager

// TODO: setup scrolling
internal object ThemesPage : Page(title = "Themes") {

  private val themeComponents = mutableListOf<ThemeComponent>()

  init {
    ThemeManager.themes.values.forEach { theme ->
      val component = ThemeComponent(theme)

      addChild(component)
      themeComponents.add(component)
    }
  }

  override fun renderComponent() {
    super.renderComponent()

    themeComponents.forEachIndexed { index, component ->
      val col = index % COLUMNS
      val row = index / COLUMNS

      val x = xPos + PADDING + col * (ThemeComponent.WIDTH + SPACING)
      val y = yPos + PADDING + row * (ThemeComponent.HEIGHT + SPACING)

      component
        .updateBounds(x, y)
        .renderComponent()
    }
  }

  private const val PADDING = 20f
  private const val COLUMNS = 3
  private const val SPACING = 20f

}
