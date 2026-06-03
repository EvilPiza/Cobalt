package org.cobalt.ui.component

import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import org.cobalt.ui.UIComponent
import org.cobalt.ui.helper.TextInputHelper
import org.cobalt.util.Dimensions
import org.cobalt.util.MouseUtils
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

  val inputHandler = TextInputHelper(FONT_SIZE, type == TextInputType.PASSWORD)
  private var xOffset: Float = 0f

  override fun renderComponent() {
    inputHandler.updateBounds(xPos, yPos, width, height)

    SkiaShapes.drawRoundedRect(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      5f,
      theme.backgroundPrimary.rgb
    )

    val textColor = if (inputHandler.focused) theme.textPrimary else theme.textMuted
    val currentText = getCurrentText()
    val maxTextWidth = width - TEXT_PADDING * 2
    val caretOffset = getCaretOffset()

    updateScrollOffset(currentText, maxTextWidth, caretOffset)

    val textX = xPos + TEXT_PADDING - xOffset
    val textY = yPos + (height - FONT_SIZE) / 2

    SkiaTransforms.pushScissor(Vec2f(xPos, yPos), Dimensions(width, height))

    drawSelection(textX, textY)

    SkiaText.drawText(
      SkiaText.regularFont,
      currentText,
      Vec2f(textX, textY),
      TextStyle(FONT_SIZE, textColor.rgb)
    )

    if (inputHandler.focused && (System.currentTimeMillis() / 500) % 2 == 0L) {
      val caretX = textX + caretOffset
      SkiaShapes.drawRect(
        Vec2f(caretX, textY),
        Dimensions(1.5f, FONT_SIZE),
        theme.textPrimary.rgb
      )
    }

    SkiaTransforms.popScissor()

    SkiaOutlines.drawRoundedOutline(
      Vec2f(xPos, yPos),
      Dimensions(width, height),
      5f,
      theme.border.rgb
    )
  }

  private fun drawSelection(textX: Float, textY: Float) {
    if (!inputHandler.hasSelection) {
      return
    }
    val selStartPrefix = if (type == TextInputType.PASSWORD) {
      "*".repeat(inputHandler.selectionStart)
    } else {
      inputHandler.text.substring(0, inputHandler.selectionStart)
    }
    val selEndPrefix = if (type == TextInputType.PASSWORD) {
      "*".repeat(inputHandler.selectionEnd)
    } else {
      inputHandler.text.substring(0, inputHandler.selectionEnd)
    }
    val selStartX = textX + SkiaText.getTextWidth(SkiaText.regularFont, selStartPrefix, FONT_SIZE)
    val selEndX = textX + SkiaText.getTextWidth(SkiaText.regularFont, selEndPrefix, FONT_SIZE)
    val selWidth = selEndX - selStartX
    val selectionColor = java.awt.Color(
      theme.textPrimary.red,
      theme.textPrimary.green,
      theme.textPrimary.blue,
      64
    ).rgb
    SkiaShapes.drawRect(
      Vec2f(selStartX, textY),
      Dimensions(selWidth, FONT_SIZE),
      selectionColor
    )
  }

  private fun getCurrentText(): String {
    if (inputHandler.usePlaceholder) {
      return placeholder
    }
    return if (type == TextInputType.PASSWORD) {
      "*".repeat(inputHandler.text.length)
    } else {
      inputHandler.text
    }
  }

  private fun getCaretOffset(): Float {
    val caretPrefix = if (type == TextInputType.PASSWORD) {
      "*".repeat(inputHandler.caretIndex)
    } else {
      inputHandler.text.substring(0, inputHandler.caretIndex)
    }
    return SkiaText.getTextWidth(SkiaText.regularFont, caretPrefix, FONT_SIZE)
  }

  private fun updateScrollOffset(currentText: String, maxTextWidth: Float, caretOffset: Float) {
    val textWidth = SkiaText.getTextWidth(SkiaText.regularFont, currentText, FONT_SIZE)
    if (textWidth <= maxTextWidth) {
      xOffset = 0f
    } else {
      if (caretOffset - xOffset > maxTextWidth) {
        xOffset = caretOffset - maxTextWidth
      } else if (caretOffset - xOffset < 0f) {
        xOffset = caretOffset
      }
    }
  }

  override fun mouseClicked(button: Int): Boolean {
    val relativeX = MouseUtils.mouseX - (xPos + TEXT_PADDING - xOffset)
    return inputHandler.mouseClicked(button, relativeX) || super.mouseClicked(button)
  }

  override fun mouseReleased(button: Int): Boolean {
    val relativeX = MouseUtils.mouseX - (xPos + TEXT_PADDING - xOffset)
    return inputHandler.mouseReleased(button, relativeX) || super.mouseReleased(button)
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
