package org.cobalt.ui.component

import org.cobalt.ui.UIComponent
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaOutlines
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaText
import org.cobalt.util.skia.TextStyle

class TextInputComponent(
  width: Float,
  height: Float,
  val placeholder: String,
  val type: TextInputType = TextInputType.DEFAULT,
  val maxLength: Int = 256,
) : UIComponent(
  width = width,
  height = height
) {

  var text: String = ""
  var focused: Boolean = false

  override fun renderComponent() {
    SkiaShapes.drawRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      5f,
      theme.backgroundPrimary.rgb
    )

    SkiaOutlines.drawRoundedOutline(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      5f,
      theme.border.rgb
    )

    val textColor: Int
    val currentText: String

    if (text.isBlank() || focused) {
      textColor = theme.textMuted.rgb
      currentText = placeholder
    } else {
      textColor = theme.textPrimary.rgb
      currentText = text
    }

    val textX = xPos + 20f
    val textY = yPos + (height - FONT_SIZE) / 2

    SkiaText.drawText(
      SkiaText.regularFont,
      currentText,
      Vec2f(textX, textY),
      TextStyle(FONT_SIZE, textColor)
    )
  }

  companion object {
    private const val FONT_SIZE = 14f
  }

}

enum class TextInputType {
  DEFAULT,
  PASSWORD
}
