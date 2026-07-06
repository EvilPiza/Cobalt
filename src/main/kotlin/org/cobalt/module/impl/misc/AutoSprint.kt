package org.cobalt.module.impl.misc

import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.TickEvent
import org.cobalt.module.Module
import org.cobalt.module.ModuleCategory
import org.cobalt.pathfinder.PathExecutor

object AutoSprint : Module(
  name = "AutoSprint",
  category = ModuleCategory.MISC,
) {

  @SubscribeEvent
  fun onTick(ignored: TickEvent.Start) {
    if (!enabled || minecraft.player == null) {
      return
    }

    minecraft.options.keySprint.isDown = true
  }

}
