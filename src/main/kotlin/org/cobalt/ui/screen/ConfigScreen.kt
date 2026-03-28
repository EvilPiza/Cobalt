package org.cobalt.ui.screen

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent

internal object ConfigScreen : Screen(Component.empty()) {

  init {
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onSkiaDraw(event: SkiaDrawEvent) {
    if (minecraft.screen != this) {
      return
    }

    // TODO: draw the UI
  }

}
