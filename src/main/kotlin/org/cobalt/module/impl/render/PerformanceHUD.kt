package org.cobalt.module.impl.render

import kotlin.math.roundToInt
import org.cobalt.Cobalt.minecraft
import org.cobalt.module.ModuleCategory
import org.cobalt.module.RenderableModule
import org.cobalt.ui.ColorPalette
import org.cobalt.util.ServerUtils
import org.cobalt.util.skia.SkiaRenderer

private const val DEFAULT_OFFSET = 5.0f

object PerformanceHUD : RenderableModule(
  name = "Performance HUD",
  category = ModuleCategory.RENDER,
  xPos = DEFAULT_OFFSET,
  yPos = DEFAULT_OFFSET,
) {

  private const val PADDING = 25f
  private const val CORNER_RADIUS = 5f
  private const val OUTLINE_THICKNESS = 2f
  private const val FONT_SIZE = 16f
  private const val TEXT_SPACING = 5f
  private const val DIVIDER_HALF_HEIGHT = 10f
  private const val PANEL_HEIGHT = 50f
  private const val MID_FACTOR = 0.5f
  private const val DIVIDER_GAP = PADDING / 2 + TEXT_SPACING

  override fun renderModule() {
    val width = getWidth()
    val height = getHeight()
    val centerY = yPos + height / 2

    SkiaRenderer.roundedRect(xPos, yPos, width, height, CORNER_RADIUS, ColorPalette.PANEL)
    SkiaRenderer.roundedOutline(xPos, yPos, width, height, CORNER_RADIUS, ColorPalette.BORDER, OUTLINE_THICKNESS)

    var currentX = xPos + PADDING
    val textY = centerY - FONT_SIZE / 2

    for ((index, stat) in getStats().withIndex()) {
      if (index > 0) {
        currentX += DIVIDER_GAP

        val midY = yPos + height * MID_FACTOR
        SkiaRenderer.line(
          currentX,
          currentX,
          midY - DIVIDER_HALF_HEIGHT,
          midY + DIVIDER_HALF_HEIGHT,
          ColorPalette.BORDER,
          OUTLINE_THICKNESS
        )

        currentX += DIVIDER_GAP
      }

      SkiaRenderer.text(SkiaRenderer.primaryFont, stat.value, currentX, textY, FONT_SIZE, ColorPalette.TEXT_PRIMARY)
      currentX += SkiaRenderer.textWidth(SkiaRenderer.primaryFont, stat.value, FONT_SIZE) + TEXT_SPACING

      SkiaRenderer.text(SkiaRenderer.primaryFont, stat.unit, currentX, textY, FONT_SIZE, ColorPalette.TEXT_DISABLED)
      currentX += SkiaRenderer.textWidth(SkiaRenderer.primaryFont, stat.unit, FONT_SIZE)
    }
  }

  override fun getWidth(): Float {
    var width = PADDING * 2

    for ((index, stat) in getStats().withIndex()) {
      if (index > 0) {
        width += PADDING + 2 * TEXT_SPACING
      }

      width += SkiaRenderer.textWidth(SkiaRenderer.primaryFont, stat.value, FONT_SIZE) + TEXT_SPACING
      width += SkiaRenderer.textWidth(SkiaRenderer.primaryFont, stat.unit, FONT_SIZE)
    }

    return width
  }

  override fun getHeight(): Float = PANEL_HEIGHT

  private fun getStats() = listOf(
    Stat(getFPS(), "FPS"),
    Stat(getTPS(), "TPS"),
    Stat(getPing(), "MS"),
  )

  private fun getFPS(): String = minecraft.fps.toString()
  private fun getTPS(): String = ServerUtils.averageTps.roundToInt().toString()
  private fun getPing(): String = ServerUtils.currentPing.toString()

  private data class Stat(val value: String, val unit: String)

}
