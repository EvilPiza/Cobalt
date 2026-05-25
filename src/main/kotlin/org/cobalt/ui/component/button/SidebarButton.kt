package org.cobalt.ui.component.button

import java.awt.Color
import org.cobalt.ui.UIComponent
import org.cobalt.ui.animation.ColorAnimation
import org.cobalt.ui.animation.EaseOutAnimation
import org.cobalt.ui.page.Page
import org.cobalt.ui.screen.ConfigScreen
import org.cobalt.util.Dimensions
import org.cobalt.util.MouseUtils
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaImages
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaText
import org.cobalt.util.skia.TextStyle
import org.cobalt.util.updateAlpha

class SidebarButton(val page: Page) : UIComponent(
  width = WIDTH,
  height = HEIGHT
) {

  private val icon = SkiaImages.loadImage(page.iconPath)
  private val colorAnimation = ColorAnimation(duration = 150L)
  private val xOffsetAnimation = EaseOutAnimation(duration = 200L)

  var selected = ConfigScreen.selectedPage == page

  override fun renderComponent() {
    val opaqueColor = colorAnimation.get(theme.transparent, theme.accentPrimary.updateAlpha(alpha = 50), !selected)
    val mainColor = colorAnimation.get(theme.transparent, theme.accentPrimary, !selected)
    val textColor = colorAnimation.get(theme.textPrimary, theme.accentPrimary, !selected)
    val xOffset = xOffsetAnimation.get(0F, TEXT_SELECTED_OFFSET, !selected)

    drawBackground(opaqueColor, mainColor)
    drawIcon(textColor, mainColor, xOffset)
    drawText(textColor, xOffset)
  }

  private fun drawBackground(opaqueColor: Color, mainColor: Color) {
    if (!selected) return

    SkiaShapes.drawRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      radius = CORNER_RADIUS,
      color = opaqueColor.rgb,
    )

    SkiaShapes.drawRoundedOutline(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      radius = CORNER_RADIUS,
      color = mainColor.rgb
    )
  }

  private fun drawIcon(textColor: Color, mainColor: Color, xOffset: Float) {
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
  }

  private fun drawText(textColor: Color, xOffset: Float) {
    val iconX = xPos + ICON_LEFT_PADDING
    val textX = iconX + ICON_SIZE + ICON_TEXT_PADDING
    val textY = yPos + height / 2F - FONT_SIZE / 2F

    SkiaText.drawText(
      SkiaText.regularFont,
      page.label,
      Vec2f(textX + xOffset, textY),
      TextStyle(fontSize = FONT_SIZE, color = textColor.rgb),
    )
  }

  override fun mouseReleased(button: Int): Boolean {
    if (MouseUtils.isHoveringOver(xPos, yPos, width, height) && button == 0) {
      ConfigScreen.selectedPage = page
      page.onClick()
      return true
    }

    return false
  }

  fun updateSelectionState(selected: Boolean) {
    if (this.selected != selected) {
      this.selected = selected
      colorAnimation.start()
      xOffsetAnimation.start()
    }
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
