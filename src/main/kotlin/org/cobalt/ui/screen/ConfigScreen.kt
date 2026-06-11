package org.cobalt.ui.screen

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.input.MouseButtonEvent
import org.cobalt.ui.UIScreen
import org.cobalt.ui.animation.BounceAnimation
import org.cobalt.ui.component.SidebarComponent
import org.cobalt.ui.component.TopbarComponent
import org.cobalt.ui.page.PageManager
import org.cobalt.ui.page.impl.ModulesPage
import org.cobalt.ui.theme.Theme
import org.cobalt.ui.theme.ThemeManager
import org.cobalt.util.WindowUtils.windowHeight
import org.cobalt.util.WindowUtils.windowWidth
import org.cobalt.util.skia.Skia

internal object ConfigScreen : UIScreen() {

  private val openAnim = BounceAnimation(400L)

  private val theme: Theme
    get() = ThemeManager.activeTheme

  init {
    components.add(SidebarComponent)
    components.add(TopbarComponent)
  }

  override fun added() {
    openAnim.start()
  }

  override fun renderSkia() {
    val centerX = windowWidth / 2f
    val centerY = windowHeight / 2f

    Skia.push()

    if (openAnim.isAnimating()) {
      val scale = openAnim.get(0f, 1f)

      Skia.translate(centerX, centerY)
      Skia.scale(scale, scale)
      Skia.translate(-centerX, -centerY)
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

    Skia.roundedOutline(
      pageX, pageY,
      interfaceWidth, interfaceHeight,
      1f, 10f, theme.border
    )

    Skia.line(
      startX, pageY,
      startX, pageY + ModulesPage.height,
      1f, theme.border
    )

    Skia.line(
      startX, startY,
      startX + TopbarComponent.width, startY,
      1f, theme.border
    )

    Skia.pop()
  }

  override fun mouseReleased(event: MouseButtonEvent): Boolean {
    return PageManager.currentPageType.page?.mouseReleased(event.button()) == true ||
      super.mouseReleased(event)
  }

  override fun extractBlurredBackground(graphics: GuiGraphicsExtractor) = Unit
  override fun extractMenuBackground(graphics: GuiGraphicsExtractor) = Unit

}
