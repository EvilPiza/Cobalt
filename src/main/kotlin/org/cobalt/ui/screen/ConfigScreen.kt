package org.cobalt.ui.screen

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.ui.UIComponent
import org.cobalt.ui.animation.BounceAnimation
import org.cobalt.ui.component.SidebarComponent
import org.cobalt.ui.page.ModulesPage
import org.cobalt.util.Vec2f
import org.cobalt.util.WindowUtils.windowHeight
import org.cobalt.util.WindowUtils.windowWidth
import org.cobalt.util.skia.SkiaTransforms

internal object ConfigScreen : Screen(Component.empty()) {

  private val openAnim = BounceAnimation(duration = 400L)
  private var currentPage: UIComponent = ModulesPage

  override fun added() {
    EventBus.register(this)
    openAnim.start()
  }

  override fun removed() {
    EventBus.unregister(this)
  }

  @SubscribeEvent
  fun onSkiaDraw(@Suppress("UnusedParameter") event: SkiaDrawEvent) {
    if (minecraft.screen != this) {
      return
    }

    val centerX = windowWidth / 2f
    val centerY = windowHeight / 2f

    SkiaTransforms.save()

    if (openAnim.isAnimating()) {
      val scale = openAnim.get(0f, 1f)

      SkiaTransforms.translate(Vec2f(centerX, centerY))
      SkiaTransforms.scale(Vec2f(scale, scale))
      SkiaTransforms.translate(Vec2f(-centerX, -centerY))
    }

    val pageX = centerX - (currentPage.width / 2f)
    val pageY = centerY - (currentPage.height / 2f)

    currentPage
      .updateBounds(pageX, pageY)
      .renderComponent()

    SkiaTransforms.restore()
  }

  override fun mouseReleased(event: MouseButtonEvent): Boolean {
    return currentPage.mouseReleased(event.button()) ||  super.mouseReleased(event)
  }

  override fun extractBlurredBackground(graphics: GuiGraphicsExtractor) = Unit
  override fun extractMenuBackground(graphics: GuiGraphicsExtractor) = Unit

}


