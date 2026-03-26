package org.cobalt.util.render.impl

import org.cobalt.util.render.Render2D
import org.cobalt.util.render.resource.Font
import org.cobalt.util.render.resource.Gradient
import org.cobalt.util.render.resource.Image

internal object SkijaRenderer2D : Render2D {

  override fun init() {

  }

  override fun render(screenWidth: Float, screenHeight: Float, block: () -> Unit) {

  }

  override fun push() {

  }

  override fun pop() {

  }

  override fun scale(x: Float, y: Float) {

  }

  override fun translate(x: Float, y: Float) {

  }

  override fun rotate(amount: Float) {

  }

  override fun globalAlpha(amount: Float) {

  }

  override fun pushScissor(x: Float, y: Float, width: Float, height: Float) {

  }

  override fun popScissor() {

  }

  override fun text(
    font: Font,
    text: String,
    x: Float,
    y: Float,
    size: Float,
    color: Int,
  ) {

  }

  override fun textBounds(
    font: Font,
    text: String,
    size: Float,
  ): Pair<Float, Float> {
    return Pair(0f, 0f)
  }

  override fun wrappedText(
    font: Font,
    text: String,
    x: Float,
    y: Float,
    wrappingWidth: Float,
    size: Float,
    color: Int,
    lineHeight: Float,
  ) {

  }

  override fun wrappedTextBounds(
    font: Font,
    text: String,
    wrappingWidth: Float,
    size: Float,
    lineHeight: Float,
  ): FloatArray {
    return floatArrayOf(0f, 0f, 0f, 0f)
  }

  override fun line(
    x1: Float,
    y1: Float,
    x2: Float,
    y2: Float,
    thickness: Float,
    color: Int,
  ) {

  }

  override fun circle(x: Float, y: Float, radius: Float, color: Int) {

  }

  override fun rect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    color: Int,
    radius: Float?,
  ) {

  }

  override fun gradientRect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    color1: Int,
    color2: Int,
    gradient: Gradient,
    radius: Float?,
  ) {

  }

  override fun halfRoundedRect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    color: Int,
    radius: Float,
    roundTop: Boolean,
  ) {

  }

  override fun hollowRect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    thickness: Float,
    color: Int,
    radius: Float?,
  ) {

  }

  override fun hollowGradientRect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    thickness: Float,
    color1: Int,
    color2: Int,
    gradient: Gradient,
    radius: Float?,
  ) {

  }

  override fun createImage(resourcePath: String): Image {
    return Image(resourcePath)
  }

  override fun image(
    image: Image,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    radius: Float?,
    colorMask: Int?,
  ) {

  }

  override fun deleteImage(image: Image) {

  }

  override fun color(color: Int) {

  }

  override fun color(color1: Int, color2: Int) {

  }

  override fun gradient(
    color1: Int,
    color2: Int,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    direction: Gradient,
  ) {

  }

}
