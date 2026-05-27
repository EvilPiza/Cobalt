package org.cobalt.ui.page

import org.cobalt.Cobalt.minecraft
import org.cobalt.ui.screen.HudEditorScreen
import org.cobalt.util.helper.TickScheduler

object PageManager {

  var currentPage: Page = Page.SCRIPTS
    private set

  fun changePage(page: Page) {
    if (page == Page.HUD) {
      TickScheduler.schedule(1L) {
        minecraft.setScreen(HudEditorScreen)
      }

      return
    }

    if (currentPage != page) {
      currentPage = page
    }
  }

}

