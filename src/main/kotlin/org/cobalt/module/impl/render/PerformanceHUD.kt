package org.cobalt.module.impl.render

import kotlin.math.roundToInt
import org.cobalt.Cobalt.minecraft
import org.cobalt.module.ModuleCategory
import org.cobalt.module.RenderableModule
import org.cobalt.ui.ColorPalette
import org.cobalt.util.ServerUtils
import org.cobalt.util.skia.SkiaRenderer

object PerformanceHUD : RenderableModule(
  name = "Performance HUD",
  category = ModuleCategory.RENDER,
  xPos = 5.0f,
  yPos = 5.0f,
) {

  private const val PADDING = 25f

  override fun renderModule() {
    val width = getWidth()
    val height = getHeight()
    val centerY = yPos + height / 2

    SkiaRenderer.roundedRect(xPos, yPos, width, height, 5f, ColorPalette.PANEL)
    SkiaRenderer.roundedOutline(xPos, yPos, width, height, 5f, ColorPalette.BORDER, 2f)

    var currentX = xPos + PADDING
    val textY = centerY - 16f / 2

    for ((index, stat) in getStats().withIndex()) {
      if (index > 0) {
        currentX += PADDING / 2 + 5f

        val midY = yPos + height * 0.5f
        SkiaRenderer.line(currentX, currentX, midY - 10f, midY + 10f, ColorPalette.BORDER, 2f)

        currentX += PADDING / 2 + 5f
      }

      SkiaRenderer.text(SkiaRenderer.primaryFont, stat.value, currentX, textY, 16f, ColorPalette.TEXT_PRIMARY)
      currentX += SkiaRenderer.textWidth(SkiaRenderer.primaryFont, stat.value, 16f) + 5f

      SkiaRenderer.text(SkiaRenderer.primaryFont, stat.unit, currentX, textY, 16f, ColorPalette.TEXT_DISABLED)
      currentX += SkiaRenderer.textWidth(SkiaRenderer.primaryFont, stat.unit, 16f)
    }
  }

  override fun getWidth(): Float {
    var width = PADDING * 2

    for ((index, stat) in getStats().withIndex()) {
      if (index > 0) {
        width += PADDING / 2 + PADDING / 2 + 10f
      }

      width += SkiaRenderer.textWidth(SkiaRenderer.primaryFont, stat.value, 16f) + 5f
      width += SkiaRenderer.textWidth(SkiaRenderer.primaryFont, stat.unit, 16f)
    }

    return width
  }

  override fun getHeight(): Float = 50f

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
