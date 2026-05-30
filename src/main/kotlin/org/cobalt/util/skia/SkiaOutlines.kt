package org.cobalt.util.skia

import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.PaintMode
import io.github.humbleui.types.RRect
import io.github.humbleui.types.Rect
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaContext.canvas

object SkiaOutlines {

  @JvmStatic
  fun drawLine(start: Vec2f, end: Vec2f, color: Int, thickness: Float = 1f) {
    val canvas = canvas ?: return

    Paint().apply {
      setColor(color)
      mode = PaintMode.STROKE
      strokeWidth = thickness.coerceAtLeast(0f)
      isAntiAlias = true
    }.use { paint ->
      canvas.drawLine(start.x, start.y, end.x, end.y, paint)
    }
  }

  @JvmStatic
  fun drawOutline(pos: Vec2f, dim: Dimensions, color: Int, thickness: Float = 1f) {
    if (!isValid(dim)) {
      return
    }

    val canvas = canvas ?: return
    val thickness = thickness.coerceAtLeast(0f)
    val half = thickness / 2f

    Paint().apply {
      setColor(color)
      mode = PaintMode.STROKE
      strokeWidth = thickness
      isAntiAlias = true
    }.use { paint ->
      canvas.drawRect(
        Rect.makeXYWH(pos.x + half, pos.y + half, dim.width - thickness, dim.height - thickness),
        paint,
      )
    }
  }

  @JvmStatic
  fun drawRoundedOutline(
    pos: Vec2f, dim: Dimensions,
    radius: Float, color: Int, thickness: Float = 1f,
  ) {
    if (!isValid(dim)) {
      return
    }

    val canvas = canvas ?: return
    val thickness = thickness.coerceAtLeast(1f)
    val half = thickness / 2f
    val innerRadius = (radius - half).coerceAtLeast(0f)

    Paint().apply {
      setColor(color)
      mode = PaintMode.STROKE
      strokeWidth = thickness
      isAntiAlias = true
    }.use { paint ->
      canvas.drawRRect(
        RRect.makeXYWH(pos.x + half, pos.y + half, dim.width - thickness, dim.height - thickness, innerRadius),
        paint
      )
    }
  }

  @JvmStatic
  fun drawCircleOutline(centerPos: Vec2f, radius: Float, color: Int, thickness: Float = 1f) {
    val canvas = canvas ?: return
    val thickness = thickness.coerceAtLeast(0f)

    Paint().apply {
      setColor(color)
      mode = PaintMode.STROKE
      strokeWidth = thickness
      isAntiAlias = true
    }.use { paint ->
      canvas.drawCircle(centerPos.x, centerPos.y, radius.coerceAtLeast(0f), paint)
    }
  }

  private fun isValid(dim: Dimensions) =
    dim.width > 0f && dim.height > 0f

}
