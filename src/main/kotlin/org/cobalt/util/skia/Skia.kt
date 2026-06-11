@file:Suppress("TooManyFunctions", "WildcardImport")

package org.cobalt.util.skia

import io.github.humbleui.skija.*
import io.github.humbleui.skija.paragraph.*
import io.github.humbleui.types.RRect
import io.github.humbleui.types.Rect
import java.awt.Color
import org.cobalt.util.skia.helper.SkiaCorner
import org.cobalt.util.skia.helper.SkiaFont
import org.cobalt.util.skia.helper.SkiaImage
import org.joml.Matrix3x2fc

object Skia {

  private val imageCache = HashMap<String, SkiaImage>()
  private val typefaces = HashMap<SkiaFont, Typeface>()
  private var canvas: Canvas? = null
  private var globalAlpha = 1f

  private val fontCollection: FontCollection by lazy {
    FontCollection().apply { setDefaultFontManager(FontMgr.getDefault()) }
  }

  @JvmStatic
  val regularFont = SkiaFont("/assets/cobalt/fonts/ProductSans-Regular.ttf")

  @JvmStatic
  val boldFont = SkiaFont("/assets/cobalt/fonts/ProductSans-Bold.ttf")

  internal fun beginFrame(canvas: Canvas) {
    this.canvas = canvas
    this.globalAlpha = 1f
  }

  internal fun endFrame() {
    canvas = null
    globalAlpha = 1f
  }

  @JvmStatic
  fun push() {
    canvas().save()
  }

  @JvmStatic
  fun pop() {
    canvas().restore()
  }

  @JvmStatic
  fun scale(x: Float, y: Float) {
    canvas().scale(x, y)
  }

  @JvmStatic
  fun translate(x: Float, y: Float) {
    canvas().translate(x, y)
  }

  @JvmStatic
  fun transform(matrix: Matrix3x2fc) {
    val skiaMatrix = Matrix33(
      matrix.m00(), matrix.m10(), matrix.m20(),
      matrix.m01(), matrix.m11(), matrix.m21(),
      0f, 0f, 1f
    )

    canvas().concat(skiaMatrix)
  }

  @JvmStatic
  fun rotate(degrees: Float) {
    canvas().rotate(degrees)
  }

  @JvmStatic
  fun globalAlpha(amount: Float) {
    globalAlpha = amount.coerceIn(0f, 1f)
  }

  @JvmStatic
  fun pushScissor(x: Float, y: Float, width: Float, height: Float) {
    canvas().save()
    canvas().clipRect(Rect.makeXYWH(x, y, width, height))
  }

  @JvmStatic
  fun popScissor() {
    canvas().restore()
  }

  @JvmStatic
  fun line(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float, color: Color) {
    paint(color, PaintMode.STROKE).use {
      it.strokeWidth = thickness
      canvas().drawLine(x1, y1, x2, y2, it)
    }
  }

  @JvmStatic
  fun rect(x: Float, y: Float, width: Float, height: Float, color: Color) {
    paint(color).use {
      canvas().drawRect(Rect.makeXYWH(x, y, width, height), it)
    }
  }

  @JvmStatic
  fun outline(x: Float, y: Float, width: Float, height: Float, thickness: Float, color: Color) {
    paint(color, PaintMode.STROKE).use {
      it.strokeWidth = thickness
      canvas().drawRect(Rect.makeXYWH(x, y, width, height), it)
    }
  }

  @JvmStatic
  fun roundedRect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    radius: Float,
    color: Color,
    corners: Array<SkiaCorner> = SkiaCorner.ALL,
  ) {
    val radii = radii(radius, corners)

    paint(color).use {
      canvas().drawRRect(RRect.makeComplexXYWH(x, y, width, height, radii), it)
    }
  }

  @JvmStatic
  fun roundedOutline(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    thickness: Float,
    radius: Float,
    color: Color,
    corners: Array<SkiaCorner> = SkiaCorner.ALL
  ) {
    val radii = radii(radius, corners)

    paint(color, PaintMode.STROKE).use {
      it.strokeWidth = thickness
      canvas().drawRRect(RRect.makeComplexXYWH(x, y, width, height, radii), it)
    }
  }

  @JvmStatic
  fun text(font: SkiaFont, text: String, x: Float, y: Float, size: Float, color: Color) {
    val skiaFont = font(font, size)

    TextLine.make(text, skiaFont).use { line ->
      val baseline = y - line.ascent

      paint(color).use { fill ->
        canvas().drawTextLine(line, x, baseline, fill)
      }
    }
  }

  @JvmStatic
  fun textWidth(font: SkiaFont, text: String, size: Float): Float {
    val skiaFont = font(font, size)

    TextLine.make(text, skiaFont).use { line ->
      return line.width
    }
  }

  @JvmStatic
  fun wrappedText(
    font: SkiaFont,
    text: String,
    x: Float,
    y: Float,
    width: Float,
    size: Float,
    color: Color
  ) {
    buildParagraph(typeface(font), text, width, color, size).paint(canvas(), x, y)
  }

  @JvmStatic
  fun wrappedTextHeight(
    font: SkiaFont,
    text: String,
    maxWidth: Float,
    fontSize: Float
  ): Float {
    return buildParagraph(typeface(font), text, maxWidth, Color.BLACK, fontSize).height
  }

  @JvmStatic
  fun buildParagraph(typeface: Typeface, text: String, maxWidth: Float, color: Color, fontSize: Float): Paragraph {
    return ParagraphBuilder(ParagraphStyle(), fontCollection)
      .pushStyle(
        TextStyle().apply {
          this.color = color.rgb
          this.fontSize = fontSize
          this.typeface = typeface
        }
      )
      .addText(text)
      .build()
      .apply { layout(maxWidth) }
  }

  @JvmStatic
  fun createImage(resourcePath: String): SkiaImage {
    val image = imageCache.getOrPut(resourcePath) { SkiaImage(resourcePath) }
    image.refCount++
    return image
  }

  @JvmStatic
  fun deleteImage(image: SkiaImage) {
    image.refCount--

    if (image.refCount > 0) return

    image.close()
    imageCache.remove(image.location)
  }

  @JvmStatic
  fun image(
    image: SkiaImage,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    radius: Float? = null,
    color: Color? = null,
  ) {
    val img = image.resolve(width.toInt().coerceAtLeast(1), height.toInt().coerceAtLeast(1), color)
    val src = Rect.makeWH(img.width.toFloat(), img.height.toFloat())
    val dst = Rect.makeXYWH(x, y, width, height)

    Paint().use { paint ->
      if (color != null && !image.isSvg) {
        paint.colorFilter = ColorFilter.makeBlend(color.rgb, BlendMode.MODULATE)
      }

      paint.alphaf = globalAlpha

      val draw = {
        canvas().drawImageRect(img, src, dst, SamplingMode.MITCHELL, paint, false)
      }

      if (radius != null && radius > 0f) {
        canvas().save()
        try {
          canvas().clipRRect(RRect.makeXYWH(x, y, width, height, radius))
          draw()
        } finally {
          canvas().restore()
        }
      } else {
        draw()
      }
    }
  }

  private fun typeface(font: SkiaFont): Typeface {
    return typefaces.getOrPut(font) {
      FontMgr.getDefault().makeFromData(
        Data.makeFromBytes(font.bytes)
      ) ?: error("Failed to load font: ${font.location}")
    }
  }

  private fun font(font: SkiaFont, size: Float): Font {
    return Font(typeface(font), size)
      .setEdging(FontEdging.SUBPIXEL_ANTI_ALIAS)
  }

  private fun paint(color: Color, mode: PaintMode = PaintMode.FILL): Paint {
    return Paint().setAntiAlias(true)
      .setMode(mode)
      .setColor(color.rgb)
      .setAlphaf((color.alpha / 255f) * globalAlpha)
  }

  private fun radii(radius: Float, corners: Array<SkiaCorner>): FloatArray {
    fun has(corner: SkiaCorner) =
      corner in corners

    return floatArrayOf(
      if (has(SkiaCorner.TOP_LEFT)) radius else 0f,
      if (has(SkiaCorner.TOP_LEFT)) radius else 0f,
      if (has(SkiaCorner.TOP_RIGHT)) radius else 0f,
      if (has(SkiaCorner.TOP_RIGHT)) radius else 0f,
      if (has(SkiaCorner.BOTTOM_RIGHT)) radius else 0f,
      if (has(SkiaCorner.BOTTOM_RIGHT)) radius else 0f,
      if (has(SkiaCorner.BOTTOM_LEFT)) radius else 0f,
      if (has(SkiaCorner.BOTTOM_LEFT)) radius else 0f,
    )
  }

  private fun canvas(): Canvas {
    return canvas ?: throw IllegalStateException("Skia frame has not started")
  }

}
