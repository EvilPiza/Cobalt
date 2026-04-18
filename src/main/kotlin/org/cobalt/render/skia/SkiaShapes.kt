package org.cobalt.render.skia

import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.PaintMode
import io.github.humbleui.types.RRect
import org.cobalt.math.Dimensions
import org.cobalt.math.SimpleVec3

/** Shape and scissor drawing helpers backed by the Skia canvas. */
object SkiaShapes {
  private val canvas get() = SkiaContext.canvas

  /**
   * Draw a straight line between two points.
   *
   * @param start start point
   * @param end end point
   * @param color ARGB color
   * @param thickness line thickness
   */
  @JvmStatic
  fun line(start: SimpleVec3, end: SimpleVec3, color: Int, thickness: Float = 1f) {
    val canvas = this.canvas ?: return

    Paint().apply {
      setColor(color)
      mode = PaintMode.STROKE
      strokeWidth = thickness.coerceAtLeast(0f)
      isAntiAlias = true
    }.use { paint ->
      canvas.drawLine(start.x, start.y, end.x, end.y, paint)
    }
  }

  /**
   * Draw a filled rounded rectangle with the specified corner radius.
   *
   * @param pos the position
   * @param dim the dimensions
   * @param radius corner radius
   * @param color outline color
   */
  @JvmStatic
  fun roundedRect(pos: SimpleVec3, dim: Dimensions, radius: Float, color: Int) {
    val canvas = this.canvas ?: return

    if (dim.width <= 0f || dim.height <= 0f) return

    Paint().setColor(color).use { paint ->
      canvas.drawRRect(RRect.makeXYWH(pos.x, pos.y, dim.width, dim.height, radius.coerceAtLeast(0f)), paint)
    }
  }

  /**
   * Draw a rounded rectangle outline with the specified thickness.
   *
   * @param pos the position
   * @param dim the dimensions
   * @param radius corner radius
   * @param color outline color
   * @param thickness outline thickness
   */
  @JvmStatic
  fun roundedOutline(
    pos: SimpleVec3,
    dim: Dimensions,
    radius: Float,
    color: Int,
    thickness: Float = 1f,
  ) {
    if (!isValid(dim)) { return }

    val canvas = this.canvas ?: return

    val t = thickness.coerceAtLeast(1f)
    val half = t / 2f
    val innerRadius = (radius - half).coerceAtLeast(0f)

    Paint().apply {
      setColor(color)
      mode = PaintMode.STROKE
      strokeWidth = t
      isAntiAlias = true
    }.use { paint ->
      canvas.drawRRect(RRect.makeXYWH(pos.x + half, pos.y + half, dim.width - t, dim.height - t, innerRadius), paint)
    }
  }

  private fun isValid(dim: Dimensions) = dim.width > 0f && dim.height > 0f
}
