package org.cobalt.util.skia

import io.github.humbleui.skija.Data
import io.github.humbleui.skija.Font
import io.github.humbleui.skija.FontEdging
import io.github.humbleui.skija.FontHinting
import io.github.humbleui.skija.FontMgr
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.TextLine
import io.github.humbleui.skija.Typeface
import io.github.humbleui.skija.paragraph.FontCollection
import io.github.humbleui.skija.paragraph.Paragraph
import io.github.humbleui.skija.paragraph.ParagraphBuilder
import io.github.humbleui.skija.paragraph.ParagraphStyle
import io.github.humbleui.skija.paragraph.TextStyle as SkiaTextStyle
import java.io.IOException
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaContext.canvas

object SkiaText {

  /**
   * Primary UI font used throughout the client.
   */
  @JvmStatic
  val primaryFont: Font by lazy {
    loadFont("assets/cobalt/font/ProductSans-Bold.ttf").first
  }


  private val fonts = mutableMapOf<String, Pair<Font, Typeface>>()

  private val fontCollection: FontCollection by lazy {
    FontCollection().apply { setDefaultFontManager(FontMgr.getDefault()) }
  }

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

    val typeface = FontMgr.getDefault().makeFromData(Data.makeFromBytes(bytes))
      ?: throw IllegalArgumentException("Invalid font data: $resourcePath")

    val font = Font(typeface).apply {
      isSubpixel = false
      hinting = FontHinting.NORMAL
      edging = FontEdging.ANTI_ALIAS
    }

    Pair(font, typeface)
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
   * Draws word-wrapped text within the given max width.
   *
   * @param font font instance to use
   * @param text text content to render
   * @param pos top-left position in screen space
   * @param maxWidth maximum line width before wrapping
   * @param style text style containing font size and color
   */
  @JvmStatic
  fun drawWrappedText(font: Font, text: String, pos: Vec2f, maxWidth: Float, style: TextStyle) {
    val canvas = canvas ?: return
    val typeface = fonts.values.find { it.first == font }?.second ?: return

    buildParagraph(typeface, text, maxWidth, style).use { para ->
      para.paint(canvas, pos.x, pos.y)
    }
  }

  /**
   * Measures the total height of word-wrapped text within the given max width.
   *
   * @param font font instance to measure with
   * @param text text content to measure
   * @param maxWidth maximum line width before wrapping
   * @param fontSize font size used for measurement
   * @return total wrapped text height in pixels
   */
  @JvmStatic
  fun getWrappedTextHeight(font: Font, text: String, maxWidth: Float, fontSize: Float): Float {
    val typeface = fonts.values.find { it.first == font }?.second ?: return 0f

    return buildParagraph(typeface, text, maxWidth, TextStyle(fontSize, 0)).use { para ->
      para.height
    }
  }

  private fun buildParagraph(typeface: Typeface, text: String, maxWidth: Float, style: TextStyle): Paragraph {
    return ParagraphBuilder(ParagraphStyle(), fontCollection)
      .pushStyle(
        SkiaTextStyle().apply {
          color = style.color
          fontSize = style.fontSize
          this.typeface = typeface
        }
      )
      .addText(text)
      .build()
      .apply { layout(maxWidth) }
  }

}

/**
 * Immutable text rendering style.
 *
 * @property fontSize font size in pixels
 * @property color ARGB color value
 */
data class TextStyle(val fontSize: Float, val color: Int)
