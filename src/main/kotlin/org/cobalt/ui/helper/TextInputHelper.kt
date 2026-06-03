package org.cobalt.ui.helper

import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import org.cobalt.util.MouseUtils
import org.cobalt.util.skia.SkiaText

class TextInputHelper(
  private val fontSize: Float,
  startText: String = "",
) {

  var text = startText
    private set

  var focused = false
    private set

  val usePlaceholder: Boolean
    get() = text.isBlank() && !focused

  private var xPos: Float = 0f
  private var yPos: Float = 0f
  private var width: Float = 0f
  private var height: Float = 0f

  fun updateBounds(xPos: Float, yPos: Float, width: Float, height: Float) {
    this.xPos = xPos
    this.yPos = yPos
    this.width = width
    this.height = height
  }

  fun mouseReleased(button: Int): Boolean {
    if (button != 0) {
      return false
    }

    val hoveringOver = MouseUtils.isHoveringOver(xPos, yPos, width, height)
    focused = hoveringOver
    return hoveringOver
  }

  fun charTyped(input: CharacterEvent): Boolean {
    return false
  }

  fun keyPressed(input: KeyEvent): Boolean {
    return false
  }

  private fun getTextWidth(str: String) =
    SkiaText.getTextWidth(SkiaText.regularFont, str, fontSize)

}
