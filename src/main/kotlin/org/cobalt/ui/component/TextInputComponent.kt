package org.cobalt.ui.component

import org.cobalt.ui.UIComponent
import org.cobalt.ui.helper.TextInputHelper
import org.cobalt.util.skia.Skia

class TextInputComponent(
  width: Float,
  height: Float,
  val placeholder: String,
  val fontSize: Float,
  val type: Type = Type.DEFAULT,
) : UIComponent(
  width = width,
  height = height
) {

  private val inputHandler = TextInputHelper(fontSize, type)

  override fun renderComponent() {
    Skia.roundedRect(
      xPos, yPos,
      width, height,
      5f, theme.backgroundPrimary
    )

    val textColor = if (inputHandler.focused) theme.textPrimary else theme.textMuted
    val textX = xPos + TEXT_PADDING
    val textY = yPos + (height - fontSize) / 2

    Skia.pushScissor(xPos, yPos, width, height)

    Skia.text(
      Skia.regularFont,
      getCurrentText(),
      textX, textY,
      fontSize, textColor
    )

    Skia.popScissor()

    Skia.roundedOutline(
      xPos, yPos,
      width, height,
      1f, 5f, theme.border
    )
  }

  private fun getCurrentText(): String {
    if (inputHandler.usePlaceholder) {
      return placeholder
    }
    return if (type == Type.PASSWORD) {
      "*".repeat(inputHandler.text.length)
    } else {
      inputHandler.text
    }
  }

  companion object {
    private const val TEXT_PADDING = 13f
  }

  enum class Type {
    DEFAULT,
    PASSWORD
  }

}

