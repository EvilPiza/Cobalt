package org.cobalt.ui.screen

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent

/**
 * Screen instance used for editing HUD elements. The object registers itself
 * on the global event bus to receive Skia draw callbacks while the screen
 * is active.
 */
object HudEditorScreen : Screen(Component.empty()) {

  init {
    EventBus.register(this)
  }

  /**
   * Handle Skia draw events to render the HUD editor overlay when this
   * screen is the currently displayed Minecraft screen.
   *
   * @param event the Skia draw event (unused in the current implementation)
   */
  @SubscribeEvent
  fun onSkiaDraw(@Suppress("UNUSED_PARAMETER") event: SkiaDrawEvent) {
    if (minecraft.screen != this) {
      return
    }

    // TODO: draw the actual HUD editor UI here..
  }

}
