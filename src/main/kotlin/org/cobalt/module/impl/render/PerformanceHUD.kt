package org.cobalt.module.impl.render

import kotlin.math.roundToInt
import org.cobalt.Cobalt.minecraft
import org.cobalt.module.Module
import org.cobalt.module.ModuleCategory
import org.cobalt.module.RenderProperties
import org.cobalt.module.Renderable
import org.cobalt.ui.ColorPalette
import org.cobalt.util.Dimensions
import org.cobalt.util.ServerUtils
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaText
import org.cobalt.util.skia.TextStyle

internal object PerformanceHUD : Module(
  name = "Performance HUD",
  category = ModuleCategory.RENDER,
), Renderable {

  override var renderProps = RenderProperties()

  private const val PADDING = 25f
  private const val CORNER_RADIUS = 5f
  private const val OUTLINE_THICKNESS = 2f
  private const val FONT_SIZE = 16f
  private const val TEXT_SPACING = 5f
  private const val DIVIDER_HALF_HEIGHT = 10f
  private const val PANEL_HEIGHT = 50f
  private const val MID_FACTOR = 0.5f
  private const val DIVIDER_GAP = PADDING / 2 + TEXT_SPACING

  override fun getWidth(): Float {
    var width = PADDING * 2

    for ((index, stat) in getStats().withIndex()) {
      if (index > 0) {
        width += PADDING + 2 * TEXT_SPACING
      }

      width += SkiaText.getTextWidth(SkiaText.primaryFont, stat.value, FONT_SIZE) + TEXT_SPACING
      width += SkiaText.getTextWidth(SkiaText.primaryFont, stat.unit, FONT_SIZE)
    }

    return width
  }

  override fun getHeight(): Float {
    return PANEL_HEIGHT
  }

  override fun renderComponent() {
    val width = getWidth()
    val height = getHeight()

    drawBackground(width, height)
    drawStats(height)
  }

  private fun drawBackground(width: Float, height: Float) {
    SkiaShapes.drawRoundedRect(Vec2f(xPos, yPos), Dimensions(width, height), CORNER_RADIUS, ColorPalette.PANEL)
    SkiaShapes.drawRoundedOutline(
      Vec2f(xPos, yPos),
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
    SkiaShapes.drawLine(
      Vec2f(x, midY - DIVIDER_HALF_HEIGHT),
      Vec2f(x, midY + DIVIDER_HALF_HEIGHT),
      ColorPalette.BORDER,
      OUTLINE_THICKNESS
    )

    x += DIVIDER_GAP
    return x
  }

  private fun drawStatText(stat: Stat, startX: Float, textY: Float): Float {
    var x = startX

    SkiaText.drawText(
      SkiaText.primaryFont,
      stat.value,
      Vec2f(x, textY),
      TextStyle(FONT_SIZE, ColorPalette.TEXT_PRIMARY)
    )

    x += SkiaText.getTextWidth(SkiaText.primaryFont, stat.value, FONT_SIZE) + TEXT_SPACING

    SkiaText.drawText(
      SkiaText.primaryFont,
      stat.unit,
      Vec2f(x, textY),
      TextStyle(FONT_SIZE, ColorPalette.TEXT_DISABLED)
    )

    x += SkiaText.getTextWidth(SkiaText.primaryFont, stat.unit, FONT_SIZE)

    return x
  }

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
