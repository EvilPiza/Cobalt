package org.cobalt.ui.screen

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.ui.UIComponent
import org.cobalt.ui.animation.BounceAnimation
import org.cobalt.ui.component.SidebarComponent
import org.cobalt.ui.page.ModulesPage
import org.cobalt.util.Vec2f
import org.cobalt.util.WindowUtils.scaledHeight
import org.cobalt.util.WindowUtils.scaledWidth
import org.cobalt.util.WindowUtils.windowScale
import org.cobalt.util.skia.SkiaTransforms

internal object ConfigScreen : Screen(Component.empty()) {

  private val openAnim = BounceAnimation(duration = 400L)
  private var currentPage: UIComponent = ModulesPage

  private val sidebar = SidebarComponent()

  init {
    EventBus.register(this)
  }

  @SubscribeEvent
  fun onSkiaDraw(@Suppress("UnusedParameter") event: SkiaDrawEvent) {
    if (minecraft.screen != this) return

    val centerX = scaledWidth / 2f
    val centerY = scaledHeight / 2f

    SkiaTransforms.save()
    SkiaTransforms.scale(Vec2f(windowScale, windowScale))

    if (openAnim.isAnimating()) {
      val scale = openAnim.get(0f, 1f)

      SkiaTransforms.translate(Vec2f(centerX, centerY))
      SkiaTransforms.scale(Vec2f(scale, scale))
      SkiaTransforms.translate(Vec2f(-centerX, -centerY))
    }

    val totalWidth = sidebar.width + SIDEBAR_GAP + currentPage.width
    val sidebarX = centerX - (totalWidth / 2f)
    val sidebarY = centerY - (sidebar.height / 2f)

    sidebar
      .updateBounds(sidebarX, sidebarY)
      .renderComponent()

    val pageX = sidebarX + sidebar.width + SIDEBAR_GAP
    val pageY = centerY - (currentPage.height / 2f)

    currentPage
      .updateBounds(pageX, pageY)
      .renderComponent()

    SkiaTransforms.restore()
  }

  override fun added() {
    openAnim.start()
  }

  override fun extractBlurredBackground(graphics: GuiGraphicsExtractor) = Unit
  override fun extractMenuBackground(graphics: GuiGraphicsExtractor) = Unit

  private const val SIDEBAR_GAP = 16f

}


