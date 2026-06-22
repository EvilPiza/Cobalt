package org.cobalt.ui.component.setting.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import org.cobalt.dsl.updateAlpha
import org.cobalt.ui.animation.ColorAnimation
import org.cobalt.ui.animation.EaseOutAnimation
import org.cobalt.ui.component.setting.Setting
import org.cobalt.util.MouseUtils
import org.cobalt.util.skia.Skia

class CheckboxSetting(
  name: String,
  description: String,
  defaultValue: Boolean,
) : Setting<Boolean>(name, description, defaultValue) {

  private val colorAnimation = ColorAnimation(150L)
  private val alphaAnimation = EaseOutAnimation(150L)

  override fun read(element: JsonElement) {
    this.value = element.asBoolean
  }

  override fun write(): JsonElement {
    return JsonPrimitive(value)
  }

  override fun renderSetting() {
    val startX = xPos + width - PADDING - SIZE
    val startY = yPos + (height - SIZE) / 2

    val alpha = alphaAnimation.get(0f, 40f, !value).toInt()
    val borderColor = colorAnimation.get(theme.border, theme.accentPrimary, !value)
    val bgColor = colorAnimation.get(theme.backgroundPrimary, theme.accentPrimary, !value)

    Skia.roundedRect(
      startX, startY,
      SIZE, SIZE,
      5f, bgColor.updateAlpha(alpha)
    )

    Skia.roundedOutline(
      startX, startY,
      SIZE, SIZE,
      1f, 5f, borderColor
    )

    Skia.circle(
      startX + SIZE / 2,
      startY + SIZE / 2,
      2f, borderColor
    )
  }

  override fun mouseClicked(button: Int): Boolean {
    if (!MouseUtils.isHoveringOver(xPos, yPos, width, height)) {
      return false
    }

    value = !value
    colorAnimation.start()
    alphaAnimation.start()

    return true
  }

  companion object {
    private const val SIZE = 22.5f
  }

}
