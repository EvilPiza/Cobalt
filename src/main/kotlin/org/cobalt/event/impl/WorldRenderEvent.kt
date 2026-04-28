package org.cobalt.event.impl

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import org.cobalt.event.Event

class WorldRenderEvent(
  val context: LevelRenderContext,
) : Event()
