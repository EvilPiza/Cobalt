package org.cobalt.render.skia

import io.github.humbleui.skija.ClipMode
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.PaintMode
import io.github.humbleui.skija.Shader
import io.github.humbleui.types.RRect
import io.github.humbleui.types.Rect

/** Shape and scissor drawing helpers backed by the Skia canvas. */
object SkiaShapes {
  private var scissorStackDepth = 0
  private val canvas get() = SkiaContext.canvas

  /**
   * Push a scissor/clip rectangle onto the canvas stack. Subsequent draws
   * will be clipped to the rectangle.
   *
   * @param x clip rectangle x coordinate
   * @param y clip rectangle y coordinate
   * @param width clip rectangle width
   * @param height clip rectangle height
   */
  @JvmStatic
  fun pushScissor(x: Float, y: Float, width: Float, height: Float) {
    val canvas = this.canvas ?: return

    if (width <= 0 || height <= 0) return

    canvas.save()
    canvas.clipRect(Rect.makeXYWH(x, y, width, height), ClipMode.INTERSECT, true)
    scissorStackDepth++
  }

  /** Pop the last scissor/clip rectangle and restore the previous canvas state. */
  @JvmStatic
  fun popScissor() {
    if (scissorStackDepth <= 0) return
    canvas?.restore()
    scissorStackDepth--
  }

  /**
   * Draw a straight line between two points.
   *
   * @param x1 start x
   * @param x2 end x
   * @param y1 start y
   * @param y2 end y
   * @param color ARGB color
   * @param thickness line thickness
   */
  @JvmStatic
  fun line(x1: Float, x2: Float, y1: Float, y2: Float, color: Int, thickness: Float = 1f) {
    val canvas = this.canvas ?: return

    Paint().apply {
      setColor(color)
      mode = PaintMode.STROKE
      strokeWidth = thickness.coerceAtLeast(0f)
      isAntiAlias = true
    }.use { paint ->
      canvas.drawLine(x1, y1, x2, y2, paint)
    }
  }

  /** Draw a filled rectangle. */
  @JvmStatic
  fun rect(x: Float, y: Float, width: Float, height: Float, color: Int) {
    val canvas = this.canvas ?: return
    if (width <= 0f || height <= 0f) return

    Paint().setColor(color).use { paint ->
      canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint)
    }
  }

  /**
   * Draw a stroked rectangle outline with the given thickness.
   *
   * @param x rectangle x
   * @param y rectangle y
   * @param width rectangle width
   * @param height rectangle height
   * @param color ARGB color for the outline
   * @param thickness outline thickness
   */
  @JvmStatic
  fun outline(x: Float, y: Float, width: Float, height: Float, color: Int, thickness: Float = 1f) {
    val canvas = this.canvas ?: return
    if (width <= 0f || height <= 0f) return

    val t = thickness.coerceAtLeast(0f)
    val half = t / 2f

    Paint().apply {
      setColor(color)
      mode = PaintMode.STROKE
      strokeWidth = t
      isAntiAlias = true
    }.use { paint ->
      canvas.drawRect(Rect.makeXYWH(x + half, y + half, width - t, height - t), paint)
    }
  }

  /** Draw a filled rounded rectangle with the specified corner radius. */
  @JvmStatic
  fun roundedRect(x: Float, y: Float, width: Float, height: Float, radius: Float, color: Int) {
    val canvas = this.canvas ?: return
    if (width <= 0f || height <= 0f) return

    Paint().setColor(color).use { paint ->
      canvas.drawRRect(RRect.makeXYWH(x, y, width, height, radius.coerceAtLeast(0f)), paint)
    }
  }

  /**
   * Draw a rounded rectangle outline with the specified thickness.
   *
   * @param x rectangle x
   * @param y rectangle y
   * @param width rectangle width
   * @param height rectangle height
   * @param radius corner radius
   * @param color outline color
   * @param thickness outline thickness
   */
  @JvmStatic
  fun roundedOutline(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    radius: Float,
    color: Int,
    thickness: Float = 1f,
  ) {
    val canvas = this.canvas ?: return
    if (width <= 0f || height <= 0f) return

    val t = thickness.coerceAtLeast(1f)
    val half = t / 2f
    val innerRadius = (radius - half).coerceAtLeast(0f)

    Paint().apply {
      setColor(color)
      mode = PaintMode.STROKE
      strokeWidth = t
      isAntiAlias = true
    }.use { paint ->
      canvas.drawRRect(RRect.makeXYWH(x + half, y + half, width - t, height - t, innerRadius), paint)
    }
  }

  /**
   * Draw a rectangle filled with a two-color linear gradient.
   *
   * @param x rectangle x
   * @param y rectangle y
   * @param width rectangle width
   * @param height rectangle height
   * @param colorStart start color ARGB
   * @param colorEnd end color ARGB
   * @param direction gradient direction
   */
  @JvmStatic
  fun gradientRect(
    x: Float, y: Float, width: Float, height: Float,
    colorStart: Int, colorEnd: Int, direction: SkiaGradient,
  ) {
    val canvas = this.canvas ?: return
    if (width <= 0f || height <= 0f) return

    val x1 = when (direction) {
      SkiaGradient.LEFT_TO_RIGHT -> x + width
      SkiaGradient.TOP_TO_BOTTOM -> x
    }

    val y1 = when (direction) {
      SkiaGradient.TOP_TO_BOTTOM -> y + height
      SkiaGradient.LEFT_TO_RIGHT -> y
    }

    Shader.makeLinearGradient(
      x, y, x1, y1,
      intArrayOf(colorStart, colorEnd)
    ).use { shader ->
      Paint().apply {
        this.shader = shader
        isAntiAlias = true
      }.use { paint ->
        canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint)
      }
    }
  }

  /**
   * Draw a rounded rectangle filled with a two-color linear gradient.
   *
   * @param x rectangle x
   * @param y rectangle y
   * @param width rectangle width
   * @param height rectangle height
   * @param radius corner radius
   * @param colorStart start color ARGB
   * @param colorEnd end color ARGB
   * @param direction gradient direction
   */
  @JvmStatic
  fun gradientRoundedRect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    radius: Float,
    colorStart: Int,
    colorEnd: Int,
    direction: SkiaGradient,
  ) {
    val canvas = this.canvas ?: return
    if (width <= 0f || height <= 0f) return

    val x1 = when (direction) {
      SkiaGradient.LEFT_TO_RIGHT -> x + width
      SkiaGradient.TOP_TO_BOTTOM -> x
    }

    val y1 = when (direction) {
      SkiaGradient.TOP_TO_BOTTOM -> y + height
      SkiaGradient.LEFT_TO_RIGHT -> y
    }

    Shader.makeLinearGradient(
      x, y, x1, y1,
      intArrayOf(colorStart, colorEnd)
    ).use { shader ->
      Paint().apply {
        this.shader = shader
        isAntiAlias = true
      }.use { paint ->
        canvas.drawRRect(RRect.makeXYWH(x, y, width, height, radius.coerceAtLeast(0f)), paint)
      }
    }
  }

  /**
   * Draw a rectangle with rounded corners on one side only.
   *
   * @param x rectangle x
   * @param y rectangle y
   * @param width rectangle width
   * @param height rectangle height
   * @param radius corner radius
   * @param color fill color ARGB
   * @param side which side should have rounded corners
   */
  @JvmStatic
  fun halfRoundedRect(
    x: Float, y: Float, width: Float, height: Float,
    radius: Float, color: Int, side: SkiaSide = SkiaSide.TOP,
  ) {
    val canvas = this.canvas ?: return
    if (width <= 0f || height <= 0f) return

    val r = radius.coerceAtLeast(0f)

    val radii = when (side) {
      SkiaSide.TOP -> floatArrayOf(r, r, r, r, 0f, 0f, 0f, 0f)
      SkiaSide.BOTTOM -> floatArrayOf(0f, 0f, 0f, 0f, r, r, r, r)
      SkiaSide.LEFT -> floatArrayOf(r, r, 0f, 0f, 0f, 0f, r, r)
      SkiaSide.RIGHT -> floatArrayOf(0f, 0f, r, r, r, r, 0f, 0f)
    }

    Paint().apply {
      setColor(color)
      isAntiAlias = true
    }.use { paint ->
      canvas.drawRRect(RRect.makeComplexXYWH(x, y, width, height, radii), paint)
    }
  }

}


