package org.cobalt.ui.component.button

import org.cobalt.dsl.updateAlpha
import org.cobalt.ui.UIComponent
import org.cobalt.ui.animation.ColorAnimation
import org.cobalt.ui.animation.EaseOutAnimation
import org.cobalt.ui.page.PageManager
import org.cobalt.ui.page.PageType
import org.cobalt.util.Dimensions
import org.cobalt.util.MouseUtils
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaImages
import org.cobalt.util.skia.SkiaOutlines
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaText
import org.cobalt.util.skia.TextStyle

internal class SidebarButton(val pageType: PageType) : UIComponent(
  width = WIDTH,
  height = HEIGHT
) {

  private val icon = SkiaImages.loadImage(pageType.iconPath)
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
      SkiaShapes.drawRoundedRect(
        Vec2f(xPos, yPos),
        Dimensions(width, height),
        CORNER_RADIUS,
        opaqueColor.rgb,
      )

      SkiaOutlines.drawRoundedOutline(
        Vec2f(xPos, yPos),
        Dimensions(width, height),
        CORNER_RADIUS,
        mainColor.rgb
      )
    }

    val iconX = xPos + ICON_LEFT_PADDING
    fun iconY(iconSize: Float) = yPos + (height - iconSize) / 2F

    SkiaImages.drawImage(
      icon.updateColor(textColor.rgb),
      Vec2f(iconX + xOffset, iconY(ICON_SIZE)),
      Dimensions(ICON_SIZE, ICON_SIZE)
    )

    if (selected) {
      SkiaImages.drawImage(
        selectedIcon.updateColor(mainColor.rgb),
        Vec2f(iconX, iconY(SELECTION_ICON_SIZE)),
        Dimensions(SELECTION_ICON_SIZE, SELECTION_ICON_SIZE)
      )
    }

    val textX = iconX + ICON_SIZE + ICON_TEXT_PADDING
    val textY = yPos + height / 2F - FONT_SIZE / 2F

    SkiaText.drawText(
      SkiaText.regularFont,
      pageType.label,
      Vec2f(textX + xOffset, textY),
      TextStyle(FONT_SIZE, textColor.rgb),
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

    private val selectedIcon = SkiaImages.loadImage("/assets/cobalt/ui/selected.svg")
  }

}
