package org.cobalt.ui.component.button

import org.cobalt.dsl.updateAlpha
import org.cobalt.ui.UIComponent
import org.cobalt.ui.animation.ColorAnimation
import org.cobalt.ui.animation.EaseOutAnimation
import org.cobalt.ui.page.PageManager
import org.cobalt.ui.page.PageType
import org.cobalt.util.MouseUtils
import org.cobalt.util.skia.Skia

internal class SidebarButton(val pageType: PageType) : UIComponent(
  width = WIDTH,
  height = HEIGHT
) {

  private val icon = Skia.createImage(pageType.iconPath)
  private val colorAnimation = ColorAnimation(150L)
  private val xOffsetAnimation = EaseOutAnimation(200L)
  private var previousPageType = PageType.SCRIPTS

  private val selected: Boolean
    get() = PageManager.currentPageType == pageType

  override fun renderComponent() {
    if (previousPageType != PageManager.currentPageType) {
      if (pageType == previousPageType || pageType == PageManager.currentPageType) {
        colorAnimation.start()
        xOffsetAnimation.start()
      }

      previousPageType = PageManager.currentPageType
    }

    val opaqueColor = colorAnimation.get(theme.transparent, theme.accentPrimary.updateAlpha(50), !selected)
    val mainColor = colorAnimation.get(theme.transparent, theme.accentPrimary, !selected)
    val textColor = colorAnimation.get(theme.textPrimary, theme.accentPrimary, !selected)
    val xOffset = xOffsetAnimation.get(0F, TEXT_SELECTED_OFFSET, !selected)

    if (selected) {
      Skia.roundedRect(
        xPos, yPos,
        width, height,
        CORNER_RADIUS,
        opaqueColor
      )

      Skia.roundedOutline(
        xPos, yPos,
        width, height,
        1f, CORNER_RADIUS, mainColor
      )
    }

    val iconX = xPos + ICON_LEFT_PADDING
    fun iconY(iconSize: Float) = yPos + (height - iconSize) / 2F

    Skia.image(
      icon,
      iconX + xOffset, iconY(ICON_SIZE),
      ICON_SIZE, ICON_SIZE,
      color = textColor
    )

    if (selected) {
      Skia.image(
        selectedIcon,
        iconX, iconY(SELECTION_ICON_SIZE),
        SELECTION_ICON_SIZE, SELECTION_ICON_SIZE,
        color = mainColor
      )
    }

    val textX = iconX + ICON_SIZE + ICON_TEXT_PADDING
    val textY = yPos + height / 2F - FONT_SIZE / 2F

    Skia.text(
      Skia.regularFont,
      pageType.label,
      textX + xOffset, textY,
      FONT_SIZE, textColor
    )
  }

  override fun mouseReleased(button: Int): Boolean {
    if (MouseUtils.isHoveringOver(xPos, yPos, width, height) && button == 0) {
      PageManager.changePage(pageType)
      return true
    }

    return false
  }

  companion object {
    const val WIDTH = 220f
    const val HEIGHT = 45f

    private const val CORNER_RADIUS = 5f
    private const val FONT_SIZE = 13f
    private const val ICON_SIZE = 15f
    private const val ICON_TEXT_PADDING = 6f
    private const val ICON_LEFT_PADDING = 10f
    private const val SELECTION_ICON_SIZE = 13f
    private const val TEXT_SELECTED_OFFSET = 17f

    private val selectedIcon = Skia.createImage("/assets/cobalt/ui/selected.svg")
  }

}
