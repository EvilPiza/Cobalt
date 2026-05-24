package org.cobalt.ui.component.button

import java.awt.Color
import org.cobalt.event.impl.MouseButton
import org.cobalt.ui.UIComponent
import org.cobalt.ui.animation.ColorAnimation
import org.cobalt.util.Dimensions
import org.cobalt.util.MouseUtils
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaText
import org.cobalt.util.skia.TextStyle

class MainMenuButton(
  private val label: String,
  private val onClick: () -> Unit,
) : UIComponent(width = WIDTH, height = HEIGHT) {

  private var isHovered = false
  private val tintAnimation = ColorAnimation(duration = 150L)

  override fun renderComponent() {
    val hovered = MouseUtils.isHoveringOver(xPos, yPos, width, height)

    if (hovered != isHovered) {
      isHovered = hovered
      tintAnimation.start()
    }

    drawBackground()
    drawLabel()
  }

  private fun drawBackground() {
    val tint = theme.accentPrimary.let {
      Color(it.red, it.green, it.blue, TINT_ALPHA)
    }

    fun animateTint(base: Color) = tintAnimation.get(
      start = base,
      end = blendOver(tint, base),
      reverse = !isHovered,
    )

    SkiaShapes.drawRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      radius = CORNER_RADIUS,
      color = animateTint(theme.backgroundPrimary).rgb,
    )

    SkiaShapes.drawRoundedOutline(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      radius = CORNER_RADIUS,
      color = animateTint(theme.border).rgb,
    )
  }

  private fun drawLabel() {
    val textWidth = SkiaText.getTextWidth(SkiaText.regularFont, label, FONT_SIZE)
    val textX = xPos + (width - textWidth) / 2f
    val textY = yPos + (height - FONT_SIZE) / 2f

    SkiaText.drawText(
      SkiaText.regularFont,
      label,
      Vec2f(textX, textY),
      TextStyle(fontSize = FONT_SIZE, color = Color.WHITE.rgb),
    )
  }

  override fun mouseClicked(button: Int): Boolean {
    if (isHovered && button == MouseButton.LEFT.ordinal) {
      onClick.invoke()
      return true
    }

    return false
  }

  private fun blendOver(foreground: Color, background: Color): Color {
    val fgAlpha = foreground.alpha / MAX_COLOR_CHANNEL.toFloat()
    val outAlpha = fgAlpha + fgAlpha * (1f - fgAlpha)

    if (outAlpha == 0f) {
      return Color.BLACK
    }

    val blend = { fgChannel: Int, bgChannel: Int ->
      ((fgChannel * fgAlpha + bgChannel * fgAlpha * (1f - fgAlpha)) / outAlpha)
        .toInt()
        .coerceIn(0, MAX_COLOR_CHANNEL)
    }

    return Color(
      blend(foreground.red, background.red),
      blend(foreground.green, background.green),
      blend(foreground.blue, background.blue),
      (outAlpha * MAX_COLOR_CHANNEL).toInt().coerceIn(0, MAX_COLOR_CHANNEL),
    )
  }

  companion object {
    private const val FONT_SIZE = 16f
    private const val CORNER_RADIUS = 10f
    private const val TINT_ALPHA = 40
    private const val MAX_COLOR_CHANNEL = 255

    const val WIDTH = 250f
    const val HEIGHT = 50f
  }

}
