package org.cobalt.util.skia

import io.github.humbleui.skija.Data
import io.github.humbleui.skija.Font
import io.github.humbleui.skija.FontEdging
import io.github.humbleui.skija.FontHinting
import io.github.humbleui.skija.FontMgr
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.TextLine
import java.io.IOException
import org.cobalt.math.Vec2f
import org.cobalt.util.skia.SkiaContext.canvas

/**
 * Utility for font loading and text rendering via Skia.
 */
object SkiaText {

  /**
   * Primary UI font used throughout the client.
   */
  @JvmField
  val primaryFont: Font = loadFont("assets/cobalt/font/ProductSans-Bold.ttf")

  private val fonts = mutableMapOf<String, Font>()

  /**
   * Loads a font from classpath resources and caches it by path.
   *
   * @param resourcePath classpath path to a font file
   * @return loaded [Font] instance configured for UI rendering
   *
   * @throws IOException if the resource cannot be found
   * @throws IllegalArgumentException if the font bytes are invalid
   */
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

  /**
   * Draws text at the given position using the provided font and style.
   *
   * No-op when no canvas is available.
   *
   * @param font font instance to use
   * @param text text content to render
   * @param pos target position in screen space
   * @param style text style containing font size and color
   */
  @JvmStatic
  fun drawText(font: Font, text: String, pos: Vec2f, style: TextStyle) {
    val canvas = canvas ?: return

    font.size = style.fontSize

    TextLine.make(text, font).use { line ->
      val baseline = pos.y - line.ascent - 1f

      Paint().setColor(style.color).use { paint ->
        canvas.drawTextLine(line, pos.x, baseline, paint)
      }
    }
  }

  /**
   * Measures the rendered width of text for the given font size.
   *
   * @param font font instance to measure with
   * @param text text content to measure
   * @param fontSize font size used for measurement
   * @return text width in pixels
   */
  @JvmStatic
  fun getTextWidth(font: Font, text: String, fontSize: Float): Float {
    font.size = fontSize

    TextLine.make(text, font).use { line ->
      return line.width
    }
  }

  /**
   * Immutable text rendering style.
   *
   * @property fontSize font size in pixels
   * @property color ARGB color value
   */
  data class TextStyle(val fontSize: Float, val color: Int)

}
