package org.cobalt.module.impl.visual

import org.cobalt.module.ModuleCategory
import org.cobalt.module.type.RenderableModule
import org.cobalt.util.ServerUtils
import org.cobalt.util.skia.Skia
import kotlin.math.roundToInt

internal object PerformanceHUD : RenderableModule(
  name = "PerformanceHUD",
  category = ModuleCategory.VISUAL,
) {

  override val width: Float
    get() {
      var width = PADDING * 2

      for ((index, stat) in getStats().withIndex()) {
        if (index > 0) {
          width += PADDING + 2 * TEXT_SPACING
        }

        width += Skia.textWidth(Skia.boldFont, stat.value, FONT_SIZE) + TEXT_SPACING
        width += Skia.textWidth(Skia.boldFont, stat.unit, FONT_SIZE)
      }

      return width
    }

  override val height: Float
    get() = 50f

  override fun renderComponent() {
    Skia.roundedRect(
      xPos, yPos, width, height,
      5f, theme.backgroundPrimary
    )

    var currentX = xPos + PADDING
    val centerY = yPos + height / 2
    val textY = centerY - FONT_SIZE / 2

    for ((index, stat) in getStats().withIndex()) {
      if (index > 0) {
        val dividerX = currentX + DIVIDER_GAP
        val midY = yPos + height / 2

        Skia.line(
          dividerX, midY - DIVIDER_HALF_HEIGHT,
          dividerX, midY + DIVIDER_HALF_HEIGHT,
          2f, theme.border
        )

        currentX = dividerX + DIVIDER_GAP
      }

      var textX = currentX

      Skia.text(
        Skia.boldFont,
        stat.value,
        textX, textY,
        FONT_SIZE, theme.textPrimary
      )

      textX += Skia.textWidth(Skia.boldFont, stat.value, FONT_SIZE) + TEXT_SPACING

      Skia.text(
        Skia.boldFont,
        stat.unit,
        textX, textY,
        FONT_SIZE, theme.textDisabled
      )

      currentX = textX + Skia.textWidth(Skia.boldFont, stat.unit, FONT_SIZE)
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
