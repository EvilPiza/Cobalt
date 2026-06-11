package org.cobalt.ui.helper

import kotlin.math.abs
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import org.cobalt.util.MouseUtils
import org.cobalt.util.skia.Skia
import org.lwjgl.glfw.GLFW

class TextInputHelper(
  private val fontSize: Float,
  val isPassword: Boolean = false,
  startText: String = "",
) {

  var text = startText
    private set

  var focused = false
    private set

  var caretIndex = startText.length
    private set

  var selectionStart = -1
    private set

  var selectionEnd = -1
    private set

  val hasSelection: Boolean
    get() = selectionStart != -1 && selectionStart != selectionEnd

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

  fun mouseClicked(button: Int, relativeX: Float): Boolean {
    return handleMouse(button, relativeX)
  }

  fun mouseReleased(button: Int, relativeX: Float): Boolean {
    return handleMouse(button, relativeX)
  }

  fun clearSelection() {
    selectionStart = -1
    selectionEnd = -1
  }

  private fun handleMouse(button: Int, relativeX: Float): Boolean {
    if (button != 0) {
      return false
    }

    val hoveringOver = MouseUtils.isHoveringOver(xPos, yPos, width, height)
    focused = hoveringOver

    if (hoveringOver) {
      caretIndex = calculateCaretIndex(relativeX)
      clearSelection()
    }

    return hoveringOver
  }

  private fun calculateCaretIndex(relativeX: Float): Int {
    if (text.isEmpty()) {
      return 0
    }

    var bestIndex = 0
    var minDiff = Float.MAX_VALUE

    for (i in 0..text.length) {
      val prefix = if (isPassword) "*".repeat(i) else text.substring(0, i)
      val w = textWidth(prefix)
      val diff = abs(w - relativeX)

      if (diff < minDiff) {
        minDiff = diff
        bestIndex = i
      }
    }

    return bestIndex
  }

  fun charTyped(input: CharacterEvent): Boolean {
    if (focused && input.isAllowedChatCharacter) {
      val str = input.codepointAsString()

      if (hasSelection) {
        text = text.substring(0, selectionStart) + str + text.substring(selectionEnd)
        caretIndex = selectionStart + str.length
        clearSelection()
      } else {
        text = text.substring(0, caretIndex) + str + text.substring(caretIndex)
        caretIndex += str.length
      }

      return true
    }

    return false
  }

  fun keyPressed(input: KeyEvent): Boolean {
    if (!focused) {
      return false
    }

    var handled = false
    val key = input.key()

    if (key == GLFW.GLFW_KEY_A && (input.modifiers() and GLFW.GLFW_MOD_CONTROL) != 0) {
      selectionStart = 0
      selectionEnd = text.length
      caretIndex = text.length
      handled = true
    }

    if (key == GLFW.GLFW_KEY_BACKSPACE) {
      if (hasSelection) {
        text = text.substring(0, selectionStart) + text.substring(selectionEnd)
        caretIndex = selectionStart
        clearSelection()
        handled = true
      } else if (caretIndex > 0) {
        text = text.substring(0, caretIndex - 1) + text.substring(caretIndex)
        caretIndex--
        handled = true
      }
    } else if (key == GLFW.GLFW_KEY_LEFT) {
      if (hasSelection) {
        caretIndex = selectionStart
        clearSelection()
        handled = true
      } else if (caretIndex > 0) {
        caretIndex--
        handled = true
      }
    } else if (key == GLFW.GLFW_KEY_RIGHT) {
      if (hasSelection) {
        caretIndex = selectionEnd
        clearSelection()
        handled = true
      } else if (caretIndex < text.length) {
        caretIndex++
        handled = true
      }
    }

    return handled
  }

  private fun textWidth(str: String) =
    Skia.textWidth(Skia.regularFont, str, fontSize)

}
