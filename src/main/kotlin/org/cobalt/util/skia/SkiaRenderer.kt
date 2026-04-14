package org.cobalt.util.skia

import io.github.humbleui.skija.BlendMode
import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.ClipMode
import io.github.humbleui.skija.ColorFilter
import io.github.humbleui.skija.Data
import io.github.humbleui.skija.Font
import io.github.humbleui.skija.FontEdging
import io.github.humbleui.skija.FontHinting
import io.github.humbleui.skija.FontMgr
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.PaintMode
import io.github.humbleui.skija.SamplingMode
import io.github.humbleui.skija.Shader
import io.github.humbleui.skija.TextLine
import io.github.humbleui.types.RRect
import io.github.humbleui.types.Rect
import java.io.IOException
import org.cobalt.Cobalt.minecraft

 /** High-level Skia drawing helpers used by UI and module renderers.
 * Provides convenience functions for text, shapes, images and scissor management.
 */
object SkiaRenderer {
  private const val BASE_WIDTH = 1920f
  private const val BASE_HEIGHT = 1080f

  private data class ImageCacheKey(
    val identifier: String,
    val radius: Float?,
    val colorMask: Int?,
  )

  private val fonts = mutableMapOf<String, Font>()
  private val images = mutableMapOf<ImageCacheKey, SkiaImage>()
  private var scissorStackDepth = 0

  val primaryFont = loadFont("assets/cobalt/font/ProductSans-Bold.ttf")

  private val canvas: Canvas?
    get() = SkiaContext.canvas

  /** Calculate a window scale factor relative to a 1920x1080 baseline for consistent UI sizing. */
  fun getWindowScale(): Float {
    val windowWidth = minecraft.window.width.toFloat()
    val windowHeight = minecraft.window.height.toFloat()

    return minOf(windowWidth / BASE_WIDTH, windowHeight / BASE_HEIGHT)
  }

  /** Save the current Skia canvas state. */
  @JvmStatic
  fun save() =
    this.canvas?.save()

  /** Restore the previously saved Skia canvas state. */
  @JvmStatic
  fun restore() =
    this.canvas?.restore()

  /** Translate the canvas by the given x/y offset. */
  @JvmStatic
  fun translate(x: Float, y: Float) =
    this.canvas?.translate(x, y)

  /** Rotate the canvas by the given angle in degrees. */
  @JvmStatic
  fun rotate(angleDeg: Float) =
    this.canvas?.rotate(angleDeg)

  /** Scale the canvas by the specified X and Y factors. */
  @JvmStatic
  fun scale(x: Float, y: Float) =
    this.canvas?.scale(x, y)

  /** Push a scissor/clip rectangle onto the canvas stack. Subsequent draws will be clipped.
   * Coordinates are in canvas space.
   */
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

  /** Pop the last scissor/clip rectangle and restore the previous canvas state. */
  @JvmStatic
  fun popScissor() {
    if (scissorStackDepth <= 0) return
    canvas?.restore()
    scissorStackDepth--
  }

  /** Load and cache a font from the given resource path. */
  @JvmStatic
  fun loadFont(resourcePath: String) = fonts.computeIfAbsent(resourcePath) {
    val bytes = javaClass.classLoader
      ?.getResourceAsStream(resourcePath)
      ?.use { it.readAllBytes() }
      ?: throw IOException("Font resource not found: $resourcePath")

    val font = FontMgr.getDefault().makeFromData(Data.makeFromBytes(bytes))
      ?: throw IllegalArgumentException("Invalid font data: $resourcePath")

    Font(font).apply {
      isSubpixel = false
      hinting = FontHinting.NORMAL
      edging = FontEdging.ANTI_ALIAS
    }
  }

  /** Draw text using the specified font at the given position and size. */
  @JvmStatic
  fun text(font: Font, text: String, x: Float, y: Float, fontSize: Float, color: Int) {
    val canvas = this.canvas ?: return
    font.size = fontSize

    TextLine.make(text, font).use { line ->
      val baseline = y - line.ascent - 1f

      Paint().setColor(color).use { paint ->
        canvas.drawTextLine(line, x, baseline, paint)
      }
    }
  }

  /** Measure and return the width of the given text using the font and size. */
  @JvmStatic
  fun textWidth(font: Font, text: String, fontSize: Float): Float {
    font.size = fontSize

    TextLine.make(text, font).use { line ->
      return line.width
    }
  }

  /** Load and cache an image by identifier, optionally applying rounding and color masking. */
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

  /** Draw a SkiaImage raster into the specified destination rectangle. */
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

  /** Draw a straight line between two points. */
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

    if (width <= 0f || height <= 0f) {
      return
    }

    Paint().setColor(color).use { paint ->
      canvas.drawRect(Rect.makeXYWH(x, y, width, height), paint)
    }
  }

  /** Draw a stroked rectangle outline with the given thickness. */
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

  /** Draw a filled rounded rectangle with the specified corner radius. */
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

  /** Draw a rounded rectangle outline with the specified thickness. */
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

  /** Draw a rectangle filled with a two-color linear gradient. */
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

  /** Draw a rounded rectangle filled with a two-color linear gradient. */
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

  /** Draw a rectangle with rounded corners on one side only.
   *
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
