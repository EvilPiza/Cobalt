package org.cobalt.ui.component

import org.cobalt.dsl.updateAlpha
import org.cobalt.module.type.Script
import org.cobalt.ui.UIComponent
import org.cobalt.util.skia.Skia
import org.cobalt.util.skia.helper.SkiaImage

class ScriptComponent(val script: Script) : UIComponent(
  width = WIDTH,
  height = 200f
) {

  val backgroundPicture: SkiaImage? = if (script.backgroundResourcePath.isNotBlank()) {
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
      Skia.blurredImage(it, drawX, drawY, drawWidth, drawHeight, radius = 2f, cornerRadius = 5f)
      Skia.popScissor()
    }

    val alpha = if (script.enabled) 0 else 100

    Skia.roundedRect(
      xPos, yPos, width, height,
      5f, theme.backgroundPrimary.updateAlpha(alpha)
    )

    Skia.roundedOutline(
      xPos, yPos, width, height,
      1f, 5f, theme.border
    )

    Skia.text(
      Skia.regularFont, script.name,
      xPos + PADDING, yPos + height - PADDING - FONT_SIZE,
      FONT_SIZE, theme.textPrimary
    )
  }

  companion object {
    val WIDTH = (TopbarComponent.width - 60) / 2f
    private const val PADDING = 20f
    private const val FONT_SIZE = 20f
  }

}
