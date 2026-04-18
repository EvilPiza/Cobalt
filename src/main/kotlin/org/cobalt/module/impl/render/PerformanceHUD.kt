package org.cobalt.module.impl.render

import kotlin.math.roundToInt
import org.cobalt.Cobalt.minecraft
import org.cobalt.math.Dimensions
import org.cobalt.math.SimpleVec3
import org.cobalt.module.ModuleCategory
import org.cobalt.module.RenderableModule
import org.cobalt.ui.ColorPalette
import org.cobalt.util.ServerUtils
import org.cobalt.render.skia.SkiaShapes
import org.cobalt.render.skia.SkiaText

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

    drawBackground(width, height)
    drawStats(height)
  }

  private fun drawBackground(width: Float, height: Float) {
    SkiaShapes.roundedRect(SimpleVec3(xPos, yPos), Dimensions(width, height), CORNER_RADIUS, ColorPalette.PANEL)
    SkiaShapes.roundedOutline(
      SimpleVec3(xPos, yPos),
      Dimensions(width, height),
      CORNER_RADIUS,
      ColorPalette.BORDER,
      OUTLINE_THICKNESS
    )
  }

  private fun drawStats(height: Float) {
    val centerY = yPos + height / 2

    var currentX = xPos + PADDING
    val textY = centerY - FONT_SIZE / 2

    for ((index, stat) in getStats().withIndex()) {
      if (index > 0) {
        currentX = drawDivider(currentX, height)
      }

      currentX = drawStatText(stat, currentX, textY)
    }
  }

  private fun drawDivider(startX: Float, height: Float): Float {
    var x = startX + DIVIDER_GAP

    val midY = yPos + height * MID_FACTOR
    SkiaShapes.line(
      SimpleVec3(x, midY - DIVIDER_HALF_HEIGHT),
      SimpleVec3(x, midY + DIVIDER_HALF_HEIGHT),
      ColorPalette.BORDER,
      OUTLINE_THICKNESS
    )

    x += DIVIDER_GAP
    return x
  }

  private fun drawStatText(stat: Stat, startX: Float, textY: Float): Float {
    var x = startX

    SkiaText.text(
      SkiaText.primaryFont,
      stat.value,
      SimpleVec3(x, textY),
      SkiaText.TextStyle(FONT_SIZE, ColorPalette.TEXT_PRIMARY)
    )
    x += SkiaText.textWidth(SkiaText.primaryFont, stat.value, FONT_SIZE) + TEXT_SPACING

    SkiaText.text(
      SkiaText.primaryFont,
      stat.unit,
      SimpleVec3(x, textY),
      SkiaText.TextStyle(FONT_SIZE, ColorPalette.TEXT_DISABLED)
    )
    x += SkiaText.textWidth(SkiaText.primaryFont, stat.unit, FONT_SIZE)

    return x
  }

  override fun getWidth(): Float {
    var width = PADDING * 2

    for ((index, stat) in getStats().withIndex()) {
      if (index > 0) {
        width += PADDING + 2 * TEXT_SPACING
      }

      width += SkiaText.textWidth(SkiaText.primaryFont, stat.value, FONT_SIZE) + TEXT_SPACING
      width += SkiaText.textWidth(SkiaText.primaryFont, stat.unit, FONT_SIZE)
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
