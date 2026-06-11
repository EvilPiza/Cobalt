package org.cobalt.module.impl.render

import kotlin.math.roundToInt
import org.cobalt.Cobalt.minecraft
import org.cobalt.module.ModuleCategory
import org.cobalt.module.RenderableModule
import org.cobalt.util.Dimensions
import org.cobalt.util.ServerUtils
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaOutlines
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaText
import org.cobalt.util.skia.TextStyle

internal object PerformanceHUD : RenderableModule(
  name = "Performance HUD",
  category = ModuleCategory.RENDER,
) {

  override fun getWidth(): Float {
    var width = PADDING * 2

    for ((index, stat) in getStats().withIndex()) {
      if (index > 0) {
        width += PADDING + 2 * TEXT_SPACING
      }

      width += SkiaText.getTextWidth(SkiaText.boldFont, stat.value, FONT_SIZE) + TEXT_SPACING
      width += SkiaText.getTextWidth(SkiaText.boldFont, stat.unit, FONT_SIZE)
    }

    return width
  }

  override fun getHeight(): Float {
    return 50f
  }

  override fun renderComponent() {
    val width = getWidth()
    val height = getHeight()

    SkiaShapes.drawRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      5f,
      theme.backgroundPrimary.rgb
    )

    var currentX = xPos + PADDING
    val centerY = yPos + height / 2
    val textY = centerY - FONT_SIZE / 2

    for ((index, stat) in getStats().withIndex()) {
      if (index > 0) {
        val dividerX = currentX + DIVIDER_GAP
        val midY = yPos + height / 2

        SkiaOutlines.drawLine(
          Vec2f(dividerX, midY - DIVIDER_HALF_HEIGHT),
          Vec2f(dividerX, midY + DIVIDER_HALF_HEIGHT),
          theme.border.rgb,
          2f
        )

        currentX = dividerX + DIVIDER_GAP
      }

      var textX = currentX

      SkiaText.drawText(
        SkiaText.boldFont,
        stat.value,
        Vec2f(textX, textY),
        TextStyle(FONT_SIZE, theme.textPrimary.rgb)
      )

      textX += SkiaText.getTextWidth(SkiaText.boldFont, stat.value, FONT_SIZE) + TEXT_SPACING

      SkiaText.drawText(
        SkiaText.boldFont,
        stat.unit,
        Vec2f(textX, textY),
        TextStyle(FONT_SIZE, theme.textDisabled.rgb)
      )

      currentX = textX + SkiaText.getTextWidth(SkiaText.boldFont, stat.unit, FONT_SIZE)
    }
  }

  private fun getStats() = listOf(
    Stat(minecraft.fps.toString(), "FPS"),
    Stat(ServerUtils.averageTps.roundToInt().toString(), "TPS"),
    Stat(ServerUtils.averagePing.toString(), "MS"),
  )

  private const val PADDING = 25f
  private const val FONT_SIZE = 16f
  private const val TEXT_SPACING = 5f
  private const val DIVIDER_HALF_HEIGHT = 10f
  private const val DIVIDER_GAP = PADDING / 2 + TEXT_SPACING

  private data class Stat(val value: String, val unit: String)

}
