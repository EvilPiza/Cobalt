package org.cobalt.util.skia

import io.github.humbleui.skija.*
import io.github.humbleui.types.RRect
import io.github.humbleui.types.Rect
import java.io.IOException

object SkiaRenderer {

  private data class ImageCacheKey(
    val identifier: String,
    val radius: Float?,
    val colorMask: Int?,
  )

  private val fonts = mutableMapOf<String, Font>()
  private val images = mutableMapOf<ImageCacheKey, SkiaImage>()
  private var scissorStackDepth = 0

  private val canvas: Canvas?
    get() = SkiaContext.canvas

  @JvmStatic
  fun save() =
    this.canvas?.save()

  @JvmStatic
  fun restore() =
    this.canvas?.restore()

  @JvmStatic
  fun translate(x: Float, y: Float) =
    this.canvas?.translate(x, y)

  @JvmStatic
  fun rotate(angleDeg: Float) =
    this.canvas?.rotate(angleDeg)

  @JvmStatic
  fun scale(x: Float, y: Float) =
    this.canvas?.scale(x, y)

  @JvmStatic
  fun pushScissor(x: Float, y: Float, width: Float, height: Float) {
    val canvas = this.canvas ?: return

    if (width <= 0 || height <= 0) {
      return
    }

    canvas.save()
    canvas.clipRect(Rect.makeXYWH(x, y, width, height), ClipMode.INTERSECT, true)
    scissorStackDepth++
  }

  @JvmStatic
  fun popScissor() {
    if (scissorStackDepth <= 0) return
    canvas?.restore()
    scissorStackDepth--
  }

  @JvmStatic
  fun loadFont(resourcePath: String, size: Float = 16f) = fonts.computeIfAbsent("$resourcePath:$size") {
    val bytes = javaClass.classLoader
      ?.getResourceAsStream(resourcePath)
      ?.use { it.readAllBytes() }
      ?: throw IOException("Font resource not found: $resourcePath")

    val font = FontMgr.getDefault().makeFromData(Data.makeFromBytes(bytes))
      ?: throw IllegalArgumentException("Invalid font data: $resourcePath")

    Font(
      font, size
    ).apply {
      isSubpixel = false
      hinting = FontHinting.NORMAL
      edging = FontEdging.ANTI_ALIAS
    }
  }

  @JvmStatic
  fun text(font: Font, text: String, x: Float, y: Float, color: Int) {
    val canvas = this.canvas ?: return

    TextLine.make(text, font).use { line ->
      val baseline = y - line.ascent

      Paint().setColor(color).use { paint ->
        canvas.drawTextLine(line, x, baseline, paint)
      }
    }
  }

  @JvmStatic
  fun textWidth(font: Font, text: String): Float {
    TextLine.make(text, font).use { line ->
      return line.width
    }
  }

  @JvmStatic
  fun loadImage(
    identifier: String,
    radius: Float? = null,
    colorMask: Int? = null,
  ): SkiaImage {
    val normalizedRadius = radius?.takeIf { it > 0f }
    val key = ImageCacheKey(identifier, normalizedRadius, colorMask)

    return images.computeIfAbsent(key) {
      SkiaImage(identifier, normalizedRadius, colorMask)
    }
  }

  @JvmStatic
  fun image(image: SkiaImage, x: Float, y: Float, width: Float, height: Float) {
    val canvas = this.canvas ?: return
    if (width <= 0 || height <= 0) return

    val sourceImage = image.getOrGenerateRaster(width.toInt(), height.toInt()) ?: return

    Paint().use { paint ->
      image.colorMask?.let {
        paint.colorFilter = ColorFilter.makeBlend(it, BlendMode.SRC_ATOP)
      }

      if (image.radius != null && image.radius > 0f) {
        canvas.save()
        canvas.clipRRect(RRect.makeXYWH(x, y, width, height, image.radius), ClipMode.INTERSECT, true)
      }

      canvas.drawImageRect(
        sourceImage,
        Rect.makeWH(sourceImage.width.toFloat(), sourceImage.height.toFloat()),
        Rect.makeXYWH(x, y, width, height),
        SamplingMode.MITCHELL,
        paint,
        false
      )

      if (image.radius != null && image.radius > 0f) {
        canvas.restore()
      }
    }
  }

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

  @JvmStatic
  fun rect(x: Float, y: Float, width: Float, height: Float, color: Int) {
    val canvas = this.canvas ?: return

    if (width <= 0f || height <= 0f) {
      return
    }

    Paint().setColor(color).use { paint ->
      canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint)
    }
  }

  @JvmStatic
  fun outline(x: Float, y: Float, width: Float, height: Float, color: Int, thickness: Float = 1f) {
    val canvas = this.canvas ?: return

    if (width <= 0f || height <= 0f) {
      return
    }

    val thickness = thickness.coerceAtLeast(0f)
    val half = thickness / 2f

    Paint().apply {
      setColor(color)
      mode = PaintMode.STROKE
      strokeWidth = thickness
      isAntiAlias = true
    }.use { paint ->
      canvas.drawRect(
        Rect.makeXYWH(x + half, y + half, width - thickness, height - thickness),
        paint,
      )
    }
  }

  @JvmStatic
  fun roundedRect(x: Float, y: Float, width: Float, height: Float, radius: Float, color: Int) {
    val canvas = this.canvas ?: return

    if (width <= 0f || height <= 0f) {
      return
    }

    Paint().setColor(color).use { paint ->
      canvas.drawRRect(RRect.makeXYWH(x, y, width, height, radius.coerceAtLeast(0f)), paint)
    }
  }

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

    if (width <= 0f || height <= 0f) {
      return
    }

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
        RRect.makeXYWH(x + half, y + half, width - thickness, height - thickness, innerRadius),
        paint,
      )
    }
  }

  @JvmStatic
  fun gradientRect(
    x: Float, y: Float, width: Float, height: Float,
    colorStart: Int, colorEnd: Int, direction: SkiaGradient,
  ) {
    val canvas = this.canvas ?: return

    if (width <= 0f || height <= 0f) {
      return
    }

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

    if (width <= 0f || height <= 0f) {
      return
    }

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
