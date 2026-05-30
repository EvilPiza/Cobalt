package org.cobalt.util.skia

import io.github.humbleui.skija.Data
import io.github.humbleui.skija.Font
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

  private val fonts = mutableMapOf<String, Pair<Font, Typeface>>()
  private val fontCollection: FontCollection by lazy {
    FontCollection().apply { setDefaultFontManager(FontMgr.getDefault()) }
  }

  @JvmStatic
  val regularFont: Font by lazy {
    loadFont("/assets/cobalt/fonts/ProductSans-Regular.ttf").first
  }

  @JvmStatic
  val boldFont: Font by lazy {
    loadFont("/assets/cobalt/fonts/ProductSans-Bold.ttf").first
  }

  @JvmStatic
  fun loadFont(resourcePath: String) = fonts.computeIfAbsent(resourcePath) {
    val bytes = javaClass.getResourceAsStream(resourcePath)
      ?.use { it.readAllBytes() }
      ?: throw IOException("Font resource not found: $resourcePath")

    val typeface = FontMgr.getDefault().makeFromData(Data.makeFromBytes(bytes))
      ?: throw IllegalArgumentException("Invalid font data: $resourcePath")

    Pair(Font(typeface), typeface)
  }

  @JvmStatic
  fun drawText(font: Font, text: String, pos: Vec2f, style: TextStyle) {
    val canvas = canvas ?: return

    font.size = style.fontSize

    TextLine.make(text, font).use { line ->
      val baseline = pos.y - line.ascent - 1f

      Paint().apply {
        setColor(style.color)
        isAntiAlias = true
      }.use { paint ->
        canvas.drawTextLine(line, pos.x, baseline, paint)
      }
    }
  }

  @JvmStatic
  fun getTextWidth(font: Font, text: String, fontSize: Float): Float {
    font.size = fontSize

    TextLine.make(text, font).use { line ->
      return line.width
    }
  }

  @JvmStatic
  fun drawWrappedText(font: Font, text: String, pos: Vec2f, maxWidth: Float, style: TextStyle) {
    val canvas = canvas ?: return
    val typeface = fonts.values.find { it.first == font }?.second ?: return

    buildParagraph(typeface, text, maxWidth, style).use { paragraph ->
      paragraph.paint(canvas, pos.x, pos.y)
    }
  }

  @JvmStatic
  fun getWrappedTextHeight(font: Font, text: String, maxWidth: Float, fontSize: Float): Float {
    val typeface = fonts.values.find { it.first == font }?.second ?: return 0f

    return buildParagraph(typeface, text, maxWidth, TextStyle(fontSize, 0)).use { paragraph ->
      paragraph.height
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

data class TextStyle(val fontSize: Float, val color: Int)
