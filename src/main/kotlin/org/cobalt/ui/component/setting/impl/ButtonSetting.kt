package org.cobalt.ui.component.setting.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.cobalt.dsl.updateAlpha
import org.cobalt.ui.animation.ColorAnimation
import org.cobalt.ui.animation.EaseOutAnimation
import org.cobalt.ui.component.setting.Setting
import org.cobalt.util.MouseUtils
import org.cobalt.util.skia.Skia

class ButtonSetting(
  name: String,
  description: String,
  val buttonLabel: String,
  val onClick: Runnable,
) : Setting<String>(name, description, "") {

  override fun read(element: JsonElement) = Unit
  override fun write(): JsonElement = JsonPrimitive("")

  private val colorAnim = ColorAnimation(150L)
  private val alphaAnim = EaseOutAnimation(150L)
  private var wasHovering = false

  private val buttonWidth =
    Skia.textWidth(Skia.regularFont, buttonLabel, FONT_SIZE) + 30f

  override fun renderSetting() {
    val buttonWidth = buttonWidth
    val startX = xPos + width - buttonWidth - PADDING
    val startY = yPos + (height - BUTTON_HEIGHT) / 2

    val hovering = MouseUtils.isHoveringOver(startX, startY, buttonWidth, BUTTON_HEIGHT)

    if (hovering != wasHovering) {
      colorAnim.start()
      alphaAnim.start()
      wasHovering = hovering
    }

    val alpha = alphaAnim.get(0f, 40f, !hovering).toInt()
    val borderColor = colorAnim.get(theme.border, theme.accentPrimary, !hovering)
    val textColor = colorAnim.get(theme.textPrimary, theme.accentPrimary, !hovering)

    Skia.roundedRect(
      startX, startY,
      buttonWidth, BUTTON_HEIGHT,
      5f, theme.backgroundPrimary
    )

    Skia.roundedRect(
      startX, startY,
      buttonWidth, BUTTON_HEIGHT,
      5f, theme.accentPrimary.updateAlpha(alpha)
    )

    Skia.roundedOutline(
      startX, startY,
      buttonWidth, BUTTON_HEIGHT,
      1f, 5f, borderColor
    )

    val textWidth = Skia.textWidth(Skia.regularFont, buttonLabel, FONT_SIZE)

    Skia.text(
      Skia.regularFont, buttonLabel,
      startX + (buttonWidth - textWidth) / 2,
      startY + (BUTTON_HEIGHT - FONT_SIZE) / 2,
      FONT_SIZE, textColor
    )
  }

  override fun mouseClicked(button: Int): Boolean {
    val startX = xPos + width - buttonWidth - PADDING
    val startY = yPos + (height - BUTTON_HEIGHT) / 2

    if (button == 0 && MouseUtils.isHoveringOver(startX, startY, buttonWidth, BUTTON_HEIGHT)) {
      onClick.run()
      return true
    }

    return false
  }

  companion object {
    private const val BUTTON_HEIGHT = 35f
    private const val FONT_SIZE = 12f
  }

}
