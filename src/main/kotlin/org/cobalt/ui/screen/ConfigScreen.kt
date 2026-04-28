package org.cobalt.ui.screen

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.util.Vec2f
import org.cobalt.ui.animation.BounceAnimation
import org.cobalt.util.skia.SkiaTransforms

internal object ConfigScreen : Screen(Component.empty()) {

  private val openAnim = BounceAnimation(400L)

  init {
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onSkiaDraw(@Suppress("UnusedParameter") event: SkiaDrawEvent) {
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

      SkiaTransforms.save()
      SkiaTransforms.translate(Vec2f(cx, cy))
      SkiaTransforms.scale(Vec2f(scale, scale))
      SkiaTransforms.translate(Vec2f(-cx, -cy))
    }

    // TODO: draw the actual UI here..

    if (openAnim.isAnimating()) {
      SkiaTransforms.restore()
    }
  }

  override fun added() {
    openAnim.start()
  }

  override fun extractBlurredBackground(graphics: GuiGraphicsExtractor) {
    return
  }

  override fun extractMenuBackground(graphics: GuiGraphicsExtractor) {
    return
  }

}


