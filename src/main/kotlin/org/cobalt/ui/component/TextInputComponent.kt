package org.cobalt.ui.component

import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import org.cobalt.ui.UIComponent
import org.cobalt.ui.helper.TextInputHelper
import org.cobalt.util.Dimensions
import org.cobalt.util.Vec2f
import org.cobalt.util.skia.SkiaOutlines
import org.cobalt.util.skia.SkiaShapes
import org.cobalt.util.skia.SkiaText
import org.cobalt.util.skia.SkiaTransforms
import org.cobalt.util.skia.TextStyle

class TextInputComponent(
  width: Float,
  height: Float,
  val placeholder: String,
  val type: TextInputType = TextInputType.DEFAULT,
) : UIComponent(
  width = width,
  height = height
) {

  val inputHandler = TextInputHelper(FONT_SIZE)
  private var xOffset: Float = 0f

  override fun renderComponent() {
    inputHandler.updateBounds(
      xPos, yPos,
      width, height
    )

    SkiaShapes.drawRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      5f,
      theme.backgroundPrimary.rgb
    )

    val textColor = if (inputHandler.focused) {
      theme.textPrimary
    } else {
      theme.textMuted
    }

    val currentText = if (inputHandler.usePlaceholder) {
      placeholder
    } else {
      if (type == TextInputType.PASSWORD) {
        "*".repeat(inputHandler.text.length)
      } else {
        inputHandler.text
      }
    }

    val textX = xPos + TEXT_PADDING - xOffset
    val textY = yPos + (height - FONT_SIZE) / 2

    SkiaTransforms.pushScissor(
      Vec2f(xPos, yPos),
      Dimensions(width, height)
    )

    SkiaText.drawText(
      SkiaText.regularFont,
      currentText,
      Vec2f(textX, textY),
      TextStyle(FONT_SIZE, textColor.rgb)
    )

    SkiaTransforms.popScissor()

    SkiaOutlines.drawRoundedOutline(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      5f,
      theme.border.rgb
    )
  }

  override fun mouseReleased(button: Int): Boolean {
    return inputHandler.mouseReleased(button) || super.mouseReleased(button)
  }

  override fun charTyped(input: CharacterEvent): Boolean {
    return inputHandler.charTyped(input) || super.charTyped(input)
  }

  override fun keyPressed(input: KeyEvent): Boolean {
    return inputHandler.keyPressed(input) || super.keyPressed(input)
  }

  companion object {
    private const val TEXT_PADDING = 13f
    private const val FONT_SIZE = 14f
  }

}

enum class TextInputType {
  DEFAULT,
  PASSWORD
}
