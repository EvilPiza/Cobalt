package org.cobalt.ui.component

import org.cobalt.dsl.updateAlpha
import org.cobalt.module.type.Script
import org.cobalt.ui.UIComponent
import org.cobalt.ui.animation.ColorAnimation
import org.cobalt.ui.animation.EaseOutAnimation
import org.cobalt.ui.screen.ConfigScreen
import org.cobalt.util.MouseUtils
import org.cobalt.util.helper.TickScheduler
import org.cobalt.util.skia.Skia
import org.cobalt.util.skia.helper.SkiaImage

class ScriptComponent(val script: Script) : UIComponent(
  width = WIDTH,
  height = 200f
) {

  private val colorAnim = ColorAnimation(200L)
  private val alphaAnim = EaseOutAnimation(200L)
  private val backgroundPicture: SkiaImage? = if (script.backgroundResourcePath.isNotBlank()) {
    Skia.createImage(script.backgroundResourcePath)
  } else null

  override fun renderComponent() {
    backgroundPicture?.let {
      val (imageWidth, imageHeight) = Skia.imageSize(it)
      val scale = maxOf(width / imageWidth, height / imageHeight)
      val drawWidth = imageWidth * scale
      val drawHeight = imageHeight * scale
      val drawX = xPos + (width - drawWidth) / 2f
      val drawY = yPos + (height - drawHeight) / 2f

      Skia.pushScissor(xPos, yPos, width, height, 5f)
      Skia.blurredImage(
        it, drawX, drawY, drawWidth, drawHeight,
        radius = 2f, cornerRadius = 5f
      )

      Skia.popScissor()
    }

    val borderColor = colorAnim.get(theme.border, theme.textMuted, !script.enabled)
    val alpha = alphaAnim.get(0f, 255f, !script.enabled).toInt()

    Skia.roundedRect(
      xPos, yPos, width, height,
      5f, theme.backgroundPrimary.updateAlpha(100)
    )

    Skia.roundedOutline(
      xPos, yPos, width, height,
      1f, 5f, borderColor
    )

    if (script.enabled) {
      Skia.image(
        pauseIcon,
        xPos + (width - ICON_SIZE) / 2,
        yPos + (height - ICON_SIZE) / 2,
        ICON_SIZE, ICON_SIZE,
        color = theme.textMuted.updateAlpha(alpha)
      )
    }

    Skia.text(
      Skia.regularFont, script.name,
      xPos + PADDING, yPos + height - PADDING - FONT_SIZE,
      FONT_SIZE, theme.textPrimary
    )
  }

  override fun mouseReleased(button: Int): Boolean {
    if (button == 0 && MouseUtils.isHoveringOver(xPos, yPos, width, height)) {
      colorAnim.start()

      if (script.enabled) {
        script.stopScript()
      } else {
        script.startScript()
        TickScheduler.schedule(2L) { ConfigScreen.closeScreen() }
      }

      return true
    }

    return super.mouseReleased(button)
  }

  companion object {
    val WIDTH = (TopbarComponent.width - 60) / 2f
    val pauseIcon = Skia.createImage("/assets/cobalt/ui/pause.svg")

    private const val PADDING = 20f
    private const val FONT_SIZE = 20f
    private const val ICON_SIZE = 30f
  }

}
