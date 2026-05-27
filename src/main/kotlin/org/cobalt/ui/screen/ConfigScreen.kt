package org.cobalt.ui.screen

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.input.MouseButtonEvent
import org.cobalt.event.EventBus
import org.cobalt.event.annotation.SubscribeEvent
import org.cobalt.event.impl.SkiaDrawEvent
import org.cobalt.ui.UIScreen
import org.cobalt.ui.animation.BounceAnimation
import org.cobalt.ui.component.SidebarComponent
import org.cobalt.ui.page.PageManager
import org.cobalt.ui.page.impl.ModulesPage
import org.cobalt.ui.theme.Theme
import org.cobalt.ui.theme.ThemeManager
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.WindowUtils.windowHeight
import org.cobalt.util.WindowUtils.windowWidth
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaTransforms

internal object ConfigScreen : UIScreen() {

  private val openAnim =
    BounceAnimation(duration = 400L)

  private val theme: Theme
    get() = ThemeManager.activeTheme

  init {
    components.add(SidebarComponent)
  }

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

    val interfaceWidth = SidebarComponent.width + ModulesPage.width
    val interfaceHeight = SidebarComponent.height

    val pageX = centerX - (interfaceWidth / 2f)
    val pageY = centerY - (interfaceHeight / 2f)

    drawPage(pageX, pageY)
    drawSidebar(pageX, pageY)
    drawBorders(pageX, pageY)

    SkiaTransforms.restore()
  }

  private fun drawSidebar(pageX: Float, pageY: Float) {
    SidebarComponent
      .updateBounds(pageX, pageY)
      .renderComponent()
  }

  private fun drawPage(pageX: Float, pageY: Float) {
    val startX = pageX + SidebarComponent.width

    PageManager.currentPage.component
      ?.updateBounds(startX, pageY)
      ?.renderComponent()
  }

  private fun drawBorders(pageX: Float, pageY: Float) {
    val width = SidebarComponent.width + ModulesPage.width
    val height = SidebarComponent.height

    SkiaShapes.drawRoundedOutline(
      Vec2f(pageX, pageY),
      Dimensions(width, height),
      radius = CORNER_RADIUS,
      color = theme.border.rgb
    )

    val lineStartX = pageX + SidebarComponent.width
    val lineEndY = pageY + ModulesPage.height

    SkiaShapes.drawLine(
      Vec2f(lineStartX, pageY),
      Vec2f(lineStartX, lineEndY),
      color = theme.border.rgb
    )
  }

  override fun mouseReleased(event: MouseButtonEvent): Boolean {
    return PageManager.currentPage.component?.mouseReleased(event.button()) == true ||
      super.mouseReleased(event)
  }

  override fun extractBlurredBackground(graphics: GuiGraphicsExtractor) = Unit
  override fun extractMenuBackground(graphics: GuiGraphicsExtractor) = Unit

  private const val CORNER_RADIUS = 10f

}


