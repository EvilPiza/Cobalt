package org.cobalt.module.impl.misc

import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.TickEvent
import org.cobalt.module.Module
import org.cobalt.module.ModuleCategory
import org.cobalt.pathfinder.PathExecutor

object AutoSprint : Module(
  name = "AutoSprint",
  category = ModuleCategory.MISC,
) {

  override fun onEnable() {
    EventBus.register(this)
  }

  override fun onDisable() {
    EventBus.unregister(this)
  }

  @SubscribeEvent
  fun onTick(ignored: TickEvent.Start) {
    if (!enabled || minecraft.player == null) {
      return
    }

    if (PathExecutor.running && PathExecutor.config?.shouldSprint != true) {
      return
    }

    minecraft.options.keySprint.isDown = true
  }

}
