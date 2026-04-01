package org.cobalt.ui.screen

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.ui.animation.BounceAnimation
import org.cobalt.util.skia.SkiaRenderer

internal object ConfigScreen : Screen(Component.empty()) {

  private val openAnim = BounceAnimation(400L)

  init {
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onSkiaDraw(event: SkiaDrawEvent) {
    if (minecraft.screen != this) {
      return
    }

    val window = minecraft.window
    val width = window.screenWidth
    val height = window.screenHeight

    if (openAnim.isAnimating()) {
      val scale = openAnim.get(0f, 1f)
      val cx = width / 2f
      val cy = height / 2f

      SkiaRenderer.save()
      SkiaRenderer.translate(cx, cy)
      SkiaRenderer.scale(scale, scale)
      SkiaRenderer.translate(-cx, -cy)
    }

    // TODO: draw the actual UI here..

    if (openAnim.isAnimating()) {
      SkiaRenderer.restore()
    }
  }

  override fun added() {
    openAnim.start()
  }

  override fun extractBlurredBackground(graphics: GuiGraphicsExtractor) {}
  override fun extractMenuBackground(graphics: GuiGraphicsExtractor) {}

}
