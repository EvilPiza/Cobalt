package org.cobalt.util.skia

import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.Shader
import io.github.humbleui.types.RRect
import io.github.humbleui.types.Rect
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaContext.canvas

object SkiaShapes {

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
        canvas.drawRect(Rect.makeXYWH(pos.x, pos.y, dim.width, dim.height), paint)
      }
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
    val radii = buildRadii(corners, radius.coerceAtLeast(0f))

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
  fun drawCircle(centerPos: Vec2f, radius: Float, color: Int) {
    val canvas = canvas ?: return

    Paint().setColor(color).use { paint ->
      paint.isAntiAlias = true
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
    val (x1, y1) = when (direction) {
      SkiaGradient.LEFT_TO_RIGHT -> pos.x + dim.width to pos.y
      SkiaGradient.TOP_TO_BOTTOM -> pos.x to pos.y + dim.height
    }

    return Shader.makeLinearGradient(pos.x, pos.y, x1, y1, intArrayOf(colorStart, colorEnd))
  }

  private fun buildRadii(corners: List<SkiaCorner>, radius: Float): FloatArray {
    val radii = FloatArray(8)

    var value = if (hasCorner(corners, SkiaCorner.TOP_LEFT)) {
      radius
    } else {
      0f
    }

    radii[0] = value
    radii[1] = value

    value = if (hasCorner(corners, SkiaCorner.TOP_RIGHT)) {
      radius
    } else {
      0f
    }

    radii[2] = value
    radii[3] = value

    value = if (hasCorner(corners, SkiaCorner.BOTTOM_RIGHT)) {
      radius
    } else {
      0f
    }

    radii[4] = value
    radii[5] = value

    value = if (hasCorner(corners, SkiaCorner.BOTTOM_LEFT)) {
      radius
    } else {
      0f
    }

    radii[6] = value
    radii[7] = value

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

  private fun isValid(dim: Dimensions) =
    dim.width > 0f && dim.height > 0f

}
