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
  fun drawRoundedRect(
    pos: Vec2f,
    dim: Dimensions,
    radius: Float,
    color: Int,
    corners: List<SkiaCorner> = listOf(SkiaCorner.ALL),
  ) {
    if (!isValid(dim)) {
      return
    }

    val canvas = canvas ?: return
    val radius = radius.coerceAtLeast(0f)
    val radii = buildRadii(corners, radius)

    Paint().apply {
      setColor(color)
      isAntiAlias = true
    }.use { paint ->
      canvas.drawRRect(RRect.makeComplexXYWH(pos.x, pos.y, dim.width, dim.height, radii), paint)
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
  fun drawCircle(centerPos: Vec2f, radius: Float, color: Int) {
    val canvas = canvas ?: return

    Paint().setColor(color).use { paint ->
      paint.isAntiAlias = true
      canvas.drawCircle(centerPos.x, centerPos.y, radius.coerceAtLeast(0f), paint)
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

  private fun buildRadii(corners: List<SkiaCorner>, radius: Float): FloatArray {
    val radii = FloatArray(RADII_ARRAY_SIZE)

    setCornerRadii(radii, CORNER_TOP_LEFT_INDICES, hasCorner(corners, SkiaCorner.TOP_LEFT), radius)
    setCornerRadii(radii, CORNER_TOP_RIGHT_INDICES, hasCorner(corners, SkiaCorner.TOP_RIGHT), radius)
    setCornerRadii(radii, CORNER_BOTTOM_RIGHT_INDICES, hasCorner(corners, SkiaCorner.BOTTOM_RIGHT), radius)
    setCornerRadii(radii, CORNER_BOTTOM_LEFT_INDICES, hasCorner(corners, SkiaCorner.BOTTOM_LEFT), radius)

    return radii
  }

  private fun hasCorner(corners: List<SkiaCorner>, corner: SkiaCorner): Boolean {
    val relatedCorners = when (corner) {
      SkiaCorner.TOP_LEFT -> listOf(SkiaCorner.TOP_LEFT, SkiaCorner.TOP, SkiaCorner.LEFT, SkiaCorner.ALL)
      SkiaCorner.TOP_RIGHT -> listOf(SkiaCorner.TOP_RIGHT, SkiaCorner.TOP, SkiaCorner.RIGHT, SkiaCorner.ALL)
      SkiaCorner.BOTTOM_RIGHT -> listOf(SkiaCorner.BOTTOM_RIGHT, SkiaCorner.BOTTOM, SkiaCorner.RIGHT, SkiaCorner.ALL)
      SkiaCorner.BOTTOM_LEFT -> listOf(SkiaCorner.BOTTOM_LEFT, SkiaCorner.BOTTOM, SkiaCorner.LEFT, SkiaCorner.ALL)
      else -> return corner in corners
    }

    return relatedCorners.any { it in corners }
  }

  private fun setCornerRadii(radii: FloatArray, indices: Pair<Int, Int>, hasRadius: Boolean, radius: Float) {
    val value = if (hasRadius) {
      radius
    } else {
      0f
    }

    radii[indices.first] = value
    radii[indices.second] = value
  }

  private fun isValid(dim: Dimensions) = dim.width > 0f && dim.height > 0f

  private const val RADII_ARRAY_SIZE = 8
  private val CORNER_TOP_LEFT_INDICES = 0 to 1
  private val CORNER_TOP_RIGHT_INDICES = 2 to 3
  private val CORNER_BOTTOM_RIGHT_INDICES = 4 to 5
  private val CORNER_BOTTOM_LEFT_INDICES = 6 to 7

}
