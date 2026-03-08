package org.cobalt.util.ui

import org.cobalt.util.ui.style.Font
import org.cobalt.util.ui.style.Gradient
import org.cobalt.util.ui.style.Image

interface Renderer {

  fun init()
  fun render(screenWidth: Float, screenHeight: Float, block: () -> Unit)

  fun push()
  fun pop()
  fun scale(x: Float, y: Float)
  fun translate(x: Float, y: Float)
  fun rotate(amount: Float)
  fun globalAlpha(amount: Float)

  fun pushScissor(x: Float, y: Float, width: Float, height: Float)
  fun popScissor()

  fun text(font: Font, text: String, x: Float, y: Float, size: Float, color: Int)
  fun textBounds(font: Font, text: String, size: Float): Pair<Float, Float>
  fun wrappedText(
    font: Font,
    text: String,
    x: Float,
    y: Float,
    wrappingWidth: Float,
    size: Float,
    color: Int,
    lineHeight: Float = 1f,
  )

  fun wrappedTextBounds(font: Font, text: String, wrappingWidth: Float, size: Float, lineHeight: Float = 1f): FloatArray

  fun line(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float, color: Int)
  fun circle(x: Float, y: Float, radius: Float, color: Int)

  fun rect(x: Float, y: Float, width: Float, height: Float, color: Int, radius: Float? = null)
  fun gradientRect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    color1: Int,
    color2: Int,
    gradient: Gradient,
    radius: Float?,
  )

  fun halfRoundedRect(x: Float, y: Float, width: Float, height: Float, color: Int, radius: Float, roundTop: Boolean)
  fun hollowRect(x: Float, y: Float, width: Float, height: Float, thickness: Float, color: Int, radius: Float? = null)
  fun hollowGradientRect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    thickness: Float,
    color1: Int,
    color2: Int,
    gradient: Gradient,
    radius: Float?,
  )

  fun createImage(resourcePath: String): Image
  fun image(
    image: Image,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    radius: Float? = null,
    colorMask: Int? = null,
  )

  fun deleteImage(image: Image)

  fun color(color: Int)
  fun color(color1: Int, color2: Int)
  fun gradient(color1: Int, color2: Int, x: Float, y: Float, width: Float, height: Float, direction: Gradient)

}
