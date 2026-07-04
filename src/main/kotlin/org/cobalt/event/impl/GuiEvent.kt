package org.cobalt.event.impl

import net.minecraft.client.gui.screens.Screen
import org.cobalt.event.Event

abstract class GuiEvent : Event() {

  class Open(val screen: Screen) : GuiEvent()
  class Draw : GuiEvent()
  class Close(val previousScreen: Screen) : GuiEvent()

}
