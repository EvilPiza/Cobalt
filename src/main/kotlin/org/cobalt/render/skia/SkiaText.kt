package org.cobalt.render.skia

import io.github.humbleui.skija.Data
import io.github.humbleui.skija.Font
import io.github.humbleui.skija.FontEdging
import io.github.humbleui.skija.FontHinting
import io.github.humbleui.skija.FontMgr
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.TextLine
import org.cobalt.math.SimpleVec3
import java.io.IOException

/** Text drawing module */
object SkiaText {
  private val fonts = mutableMapOf<String, Font>()

  /** Primary UI font used throughout the client. */
  val primaryFont: Font = loadFont("assets/cobalt/font/ProductSans-Bold.ttf")

  private val canvas get() = SkiaContext.canvas

  /**
   * Load and cache a font
   * @param resourcePath the path
   * @return the font
   */
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
   * Text styling
   * @property fontSize font size
   * @property color color
   */
  data class TextStyle(val fontSize: Float, val color: Int)

  /**
   * Draw text
   * @param font the font
   * @param text the text
   * @param pos the position
   * @param style the style
   */
  @JvmStatic
  fun text(font: Font, text: String, pos: SimpleVec3, style: TextStyle) {
    val canvas = this.canvas ?: return

    font.size = style.fontSize

    TextLine.make(text, font).use { line ->
      val baseline = pos.y - line.ascent - 1f

      Paint().setColor(style.color).use { paint ->
        canvas.drawTextLine(line, pos.x, baseline, paint)
      }
    }
  }

  /**
   * Get text width
   * @param font the font
   * @param text the text
   * @param fontSize the font size
   * @return the width
   */
  @JvmStatic
  fun textWidth(font: Font, text: String, fontSize: Float): Float {
    font.size = fontSize

    TextLine.make(text, font).use { line ->
      return line.width
    }
  }

}
