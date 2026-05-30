package org.cobalt.ui.page

import org.cobalt.Cobalt.minecraft
import org.cobalt.ui.screen.HudEditorScreen
import org.cobalt.util.helper.TickScheduler

object PageManager {

  var currentPageType: PageType = PageType.SCRIPTS
    private set

  fun changePage(pageType: PageType) {
    if (pageType == PageType.HUD) {
      TickScheduler.schedule(1L) {
        minecraft.setScreen(HudEditorScreen)
      }

      return
    }

    if (currentPageType != pageType) {
      currentPageType = pageType
    }
  }

}

