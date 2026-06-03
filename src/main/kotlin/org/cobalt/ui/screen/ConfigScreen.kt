package org.cobalt.ui.screen

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.input.MouseButtonEvent
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.ui.UIScreen
import org.cobalt.ui.animation.BounceAnimation
import org.cobalt.ui.component.SidebarComponent
import org.cobalt.ui.component.TopbarComponent
import org.cobalt.ui.page.PageManager
import org.cobalt.ui.page.impl.ModulesPage
import org.cobalt.ui.theme.Theme
import org.cobalt.ui.theme.ThemeManager
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.WindowUtils.windowHeight
import org.cobalt.util.WindowUtils.windowWidth
import org.cobalt.util.skia.SkiaOutlines
import org.cobalt.util.skia.SkiaTransforms

internal object ConfigScreen : UIScreen() {

  private val openAnim = BounceAnimation(400L)

  private val theme: Theme
    get() = ThemeManager.activeTheme

  init {
    components.add(SidebarComponent)
    components.add(TopbarComponent)
  }

  override fun added() {
    EventBus.register(this)
    openAnim.start()
  }

  override fun removed() =
    EventBus.unregister(this)

  @SubscribeEvent
  fun onSkiaDraw(ignored: SkiaDrawEvent) {
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

    val interfaceWidth = SidebarComponent.width + ModulesPage.width
    val interfaceHeight = SidebarComponent.height

    val pageX = centerX - (interfaceWidth / 2f)
    val pageY = centerY - (interfaceHeight / 2f)

    val startX = pageX + SidebarComponent.width
    val startY = pageY + TopbarComponent.height

    PageManager.currentPageType.page
      ?.updateBounds(startX, startY)
      ?.renderComponent()

    TopbarComponent
      .updateBounds(startX, pageY)
      .renderComponent()

    SidebarComponent
      .updateBounds(pageX, pageY)
      .renderComponent()

    SkiaOutlines.drawRoundedOutline(
      Vec2f(pageX, pageY),
      Dimensions(interfaceWidth, interfaceHeight),
      10f,
      theme.border.rgb
    )

    SkiaOutlines.drawLine(
      Vec2f(startX, pageY),
      Vec2f(startX, pageY + ModulesPage.height),
      theme.border.rgb
    )

    SkiaOutlines.drawLine(
      Vec2f(startX, startY),
      Vec2f(startX + TopbarComponent.width, startY),
      theme.border.rgb
    )

    SkiaTransforms.restore()
  }

  override fun mouseReleased(event: MouseButtonEvent): Boolean {
    return PageManager.currentPageType.page?.mouseReleased(event.button()) == true ||
      super.mouseReleased(event)
  }

  override fun extractBlurredBackground(graphics: GuiGraphicsExtractor) = Unit
  override fun extractMenuBackground(graphics: GuiGraphicsExtractor) = Unit

}
