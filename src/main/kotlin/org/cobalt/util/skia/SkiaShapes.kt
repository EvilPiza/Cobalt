package org.cobalt.util.skia

import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.PaintMode
import io.github.humbleui.skija.Shader
import io.github.humbleui.types.RRect
import io.github.humbleui.types.Rect
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaContext.canvas

object SkiaShapes {

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
  fun drawRect(pos: Vec2f, dim: Dimensions, color: Int) {
    if (!isValid(dim)) {
      return
    }

    val canvas = canvas ?: return

    Paint().setColor(color).use { paint ->
      canvas.drawRect(Rect.makeXYWH(pos.x, pos.y, dim.width, dim.height), paint)
    }
  }

  @JvmStatic
  fun drawGradientRect(pos: Vec2f, dim: Dimensions, colorStart: Int, colorEnd: Int, direction: SkiaGradient) {
    if (!isValid(dim)) {
      return
    }

    val canvas = canvas ?: return

    createLinearGradientShader(pos, dim, colorStart, colorEnd, direction).use { shader ->
      Paint().apply {
        this.shader = shader
        isAntiAlias = true
      }.use { paint ->
        canvas.drawRect(
          Rect.makeXYWH(pos.x, pos.y, dim.width, dim.height),
          paint
        )
      }
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
  fun drawRoundedRect(pos: Vec2f, dim: Dimensions, radius: Float, color: Int) {
    if (!isValid(dim)) {
      return
    }

    val canvas = canvas ?: return

    Paint().setColor(color).use { paint ->
      canvas.drawRRect(RRect.makeXYWH(pos.x, pos.y, dim.width, dim.height, radius.coerceAtLeast(0f)), paint)
    }
  }

  @JvmStatic
  fun drawGradientRoundedRect(
    pos: Vec2f, dim: Dimensions,
    radius: Float, colorStart: Int, colorEnd: Int, direction: SkiaGradient,
  ) {
    if (!isValid(dim)) {
      return
    }

    val canvas = canvas ?: return

    createLinearGradientShader(pos, dim, colorStart, colorEnd, direction).use { shader ->
      Paint().apply {
        this.shader = shader
        isAntiAlias = true
      }.use { paint ->
        canvas.drawRRect(
          RRect.makeXYWH(pos.x, pos.y, dim.width, dim.height, radius.coerceAtLeast(0f)),
          paint
        )
      }
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
        RRect.makeXYWH(pos.x + half, pos.y + half, dim.width - thickness, dim.height - thickness, innerRadius), paint
      )
    }
  }

  @JvmStatic
  fun drawHalfRoundedRect(
    pos: Vec2f, dim: Dimensions,
    radius: Float, color: Int, side: SkiaSide = SkiaSide.TOP,
  ) {
    if (!isValid(dim)) {
      return
    }

    val canvas = canvas ?: return
    val radius = radius.coerceAtLeast(0f)
    val radii = buildRadii(side, radius)

    Paint().apply {
      setColor(color)
      isAntiAlias = true
    }.use { paint ->
      canvas.drawRRect(RRect.makeComplexXYWH(pos.x, pos.y, dim.width, dim.height, radii), paint)
    }
  }

  private fun createLinearGradientShader(
    pos: Vec2f,
    dim: Dimensions,
    colorStart: Int,
    colorEnd: Int,
    direction: SkiaGradient,
  ): Shader {
    val (x1, y1) = calculateGradientEnd(pos, dim, direction)

    return Shader.makeLinearGradient(
      pos.x, pos.y,
      x1, y1,
      intArrayOf(colorStart, colorEnd)
    )
  }

  private fun calculateGradientEnd(
    pos: Vec2f,
    dim: Dimensions,
    direction: SkiaGradient,
  ): Pair<Float, Float> {
    return when (direction) {
      SkiaGradient.LEFT_TO_RIGHT -> pos.x + dim.width to pos.y
      SkiaGradient.TOP_TO_BOTTOM -> pos.x to pos.y + dim.height
    }
  }

  private fun buildRadii(side: SkiaSide, radius: Float): FloatArray {
    return when (side) {
      SkiaSide.TOP -> floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f)
      SkiaSide.BOTTOM -> floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius)
      SkiaSide.LEFT -> floatArrayOf(radius, radius, 0f, 0f, 0f, 0f, radius, radius)
      SkiaSide.RIGHT -> floatArrayOf(0f, 0f, radius, radius, radius, radius, 0f, 0f)
    }
  }

  private fun isValid(dim: Dimensions) = dim.width > 0f && dim.height > 0f

}
