package org.cobalt.event.impl

import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphicsExtractor
import org.cobalt.event.Event

class HudEvent(
  val graphics: GuiGraphicsExtractor,
  val deltaTracker: DeltaTracker,
) : Event()
