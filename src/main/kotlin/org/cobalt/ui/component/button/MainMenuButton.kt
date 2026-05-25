package org.cobalt.ui.component.button

import org.cobalt.event.impl.MouseButton
import org.cobalt.ui.UIComponent
import org.cobalt.ui.animation.ColorAnimation
import org.cobalt.util.Dimensions
import org.cobalt.util.MouseUtils
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaText
import org.cobalt.util.skia.TextStyle
import org.cobalt.util.updateAlpha

class MainMenuButton(
  private val label: String,
  private val onClick: () -> Unit,
) : UIComponent(width = WIDTH, height = HEIGHT) {

  private var isHovered = false
  private val colorAnimation = ColorAnimation(duration = 150L)

  override fun renderComponent() {
    val hovered = MouseUtils.isHoveringOver(xPos, yPos, width, height)

    if (hovered != isHovered) {
      isHovered = hovered
      colorAnimation.start()
    }

    drawBackground()
    drawLabel()
  }

  private fun drawBackground() {
    val backgroundColor = colorAnimation.get(
      start = theme.backgroundPrimary.updateAlpha(alpha = 50),
      end = theme.accentPrimary.updateAlpha(alpha = 50),
      reverse = !isHovered,
    )

    val borderColor = colorAnimation.get(
      start = theme.border,
      end = theme.accentPrimary,
      reverse = !isHovered,
    )

    SkiaShapes.drawRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      radius = CORNER_RADIUS,
      color = backgroundColor.rgb,
    )

    SkiaShapes.drawRoundedOutline(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      radius = CORNER_RADIUS,
      color = borderColor.rgb,
    )
  }

  private fun drawLabel() {
    val backgroundColor = colorAnimation.get(
      start = theme.textPrimary,
      end = theme.accentPrimary,
      reverse = !isHovered,
    )

    val textWidth = SkiaText.getTextWidth(SkiaText.regularFont, label, FONT_SIZE)
    val textX = xPos + (width - textWidth) / 2f
    val textY = yPos + (height - FONT_SIZE) / 2f

    SkiaText.drawText(
      SkiaText.regularFont,
      label,
      Vec2f(textX, textY),
      TextStyle(FONT_SIZE, backgroundColor.rgb),
    )
  }

  override fun mouseClicked(button: Int): Boolean {
    if (isHovered && button == MouseButton.LEFT.ordinal) {
      onClick.invoke()
      return true
    }

    return false
  }

  companion object {
    private const val FONT_SIZE = 16f
    private const val CORNER_RADIUS = 10f

    const val WIDTH = 250f
    const val HEIGHT = 50f
  }

}
