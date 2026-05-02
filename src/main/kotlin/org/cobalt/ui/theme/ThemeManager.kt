package org.cobalt.ui.theme

object ThemeManager {

  private var activeTheme: Theme = Theme("default")

  @JvmStatic
  fun getActiveTheme(): Theme {
    return activeTheme
  }

}
