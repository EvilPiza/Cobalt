package org.cobalt.ui.component

import org.cobalt.dsl.updateAlpha
import org.cobalt.ui.UIComponent
import org.cobalt.ui.theme.Theme
import org.cobalt.ui.theme.ThemeManager
import org.cobalt.util.Dimensions
import org.cobalt.util.MouseUtils
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaOutlines
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaText
import org.cobalt.util.skia.TextStyle

class ThemeComponent(val newTheme: Theme) : UIComponent(
  width = WIDTH,
  height = HEIGHT,
) {

  override fun renderComponent() {
    SkiaShapes.drawRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      5f,
      newTheme.accentPrimary.updateAlpha(40).rgb
    )

    SkiaOutlines.drawRoundedOutline(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      5f,
      newTheme.accentPrimary.rgb
    )

    val startX = xPos + INNER_PADDING
    var startY = yPos + INNER_PADDING

    SkiaText.drawText(
      SkiaText.regularFont,
      newTheme.name,
      Vec2f(startX, startY),
      TextStyle(THEME_NAME_FONT_SIZE, theme.textPrimary.rgb)
    )

    val isActive = theme == newTheme
    val text = if (isActive) "Active" else "Inactive"
    val textColor = if (isActive) theme.success else theme.textMuted
    val statusY = startY + THEME_NAME_FONT_SIZE + TEXT_PADDING

    SkiaText.drawText(
      SkiaText.regularFont,
      text,
      Vec2f(startX, statusY),
      TextStyle(STATUS_FONT_SIZE, textColor.rgb)
    )

    startY = yPos + height - SWATCH_HEIGHT - INNER_PADDING

    SkiaShapes.drawRoundedRect(
      Vec2f(startX, startY),
      Dimensions(SWATCH_WIDTH, SWATCH_HEIGHT),
      2.5f,
      newTheme.accentPrimary.rgb
    )

    SkiaShapes.drawRoundedRect(
      Vec2f(startX + SWATCH_WIDTH + SWATCH_PADDING, startY),
      Dimensions(SWATCH_WIDTH, SWATCH_HEIGHT),
      2.5f,
      newTheme.accentSecondary.rgb
    )
  }

  override fun mouseReleased(button: Int): Boolean {
    val isHovered = MouseUtils.isHoveringOver(xPos, yPos, width, height)

    if (button != 0 || !isHovered) {
      return super.mouseReleased(button)
    }

    ThemeManager.activeTheme = newTheme
    return true
  }

  companion object {
    const val WIDTH = 206f
    const val HEIGHT = 100f

    private const val INNER_PADDING = 15f
    private const val THEME_NAME_FONT_SIZE = 18f
    private const val TEXT_PADDING = 5f
    private const val STATUS_FONT_SIZE = 12f

    private const val SWATCH_WIDTH = 40f
    private const val SWATCH_HEIGHT = 6f
    private const val SWATCH_PADDING = 10f
  }

}
