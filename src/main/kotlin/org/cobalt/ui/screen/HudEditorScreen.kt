package org.cobalt.ui.screen

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent

internal object HudEditorScreen : Screen(Component.empty()) {

  init {
    EventBus.register(this)
  }

  @Suppress("UndocumentedPublicFunction")
  @SubscribeEvent
  fun onSkiaDraw(@Suppress("UnusedParameter") event: SkiaDrawEvent) {
    if (minecraft.screen != this) {
      return
    }

    // TODO: draw the actual HUD editor UI here..
  }

}
